## Context

Spec 28 asks: given a population of `N` diploid individuals with `2N` chromosomes, and an array `A` of recessive-allele counts (one per factor), compute for each factor `j` and each generation `i ∈ 1..m` the probability that **no recessive allele remains** after `i` Wright-Fisher generations — i.e., the population has reached the all-dominant absorbing state `2N`.

The framework already has the Wright-Fisher Markov chain machinery from spec 26 (`WrightFisher.atLeast`), including the `binomialPmf` helper for building transition rows. This spec adds a third method to the `WrightFisher` object (`atLeast`, `expectedFrequencies` from spec 27, and now `fixationLogProbs`), each with its own validated input bundle.

## Goals / Non-Goals

**Goals:**
- `WrightFisherFixationProblem(n, m, recessiveCounts)` validated bundle (three parameters, with the array constraint that every element lies in `[0, 2n]`).
- `WrightFisherFixationProblemError` ADT covering the six failure modes.
- `WrightFisher.fixationLogProbs(problem): Vector[Vector[Double]]` — total over the validated input. Outer Vector is the generations dimension (length `m`), inner Vectors are the factors dimension (length `|A|`).
- Match Rosalind's sample within absolute error `< 1e-9` (the sample provides 12 significant digits, so the spec uses a tighter tolerance than usual).

**Non-Goals:**
- A new "Matrix" or "Grid" wrapper type. The output is a 2-D structure but using a bare `Vector[Vector[Double]]` matches what spec 18's `ProfileMatrix` *would* be if we didn't have row-vs.-column accessor convenience. Here we don't need row/column accessors — just element access — so a nested Vector is sufficient.
- Reusing or generalizing the `WrightFisher.atLeast` distribution-evolution code. The two methods compute different post-conditions (tail-sum vs. specific-state-mass); sharing the transition-matrix loop would muddy both. The shared `binomialPmf` helper is the right granularity.

## Decisions

### Decision 1: Three-parameter bundle with array-element validation

```scala
sealed abstract case class WrightFisherFixationProblem(
    n: Int,
    m: Int,
    recessiveCounts: Vector[Int]
)
object WrightFisherFixationProblem {
  private val MaxN: Int = 100
  private val MaxM: Int = 100
  private val MaxK: Int = 100

  def from(n: Int, m: Int, recessiveCounts: Vector[Int]): Either[Err, ...] = {
    if (n < 1)        Left(NonPositiveN(n))
    else if (n > MaxN) Left(NExceedsMaximum(n, MaxN))
    else if (m < 1)   Left(NonPositiveM(m))
    else if (m > MaxM) Left(MExceedsMaximum(m, MaxM))
    else if (recessiveCounts.size > MaxK)
      Left(TooManyRecessiveCounts(recessiveCounts.size, MaxK))
    else {
      val twoN = 2 * n
      recessiveCounts.zipWithIndex.find { case (a, _) => a < 0 || a > twoN } match {
        case Some((a, idx)) => Left(RecessiveCountOutOfRange(idx, a, twoN))
        case None => Right(new WrightFisherFixationProblem(n, m, recessiveCounts) {})
      }
    }
  }
}
```

**Validation order:** `n` lower → `n` upper → `m` lower → `m` upper → `|A|` upper → first out-of-range element in `A`. First failure wins.

`n` must be validated first because the per-element check `0 ≤ A[j] ≤ 2n` depends on `n`. The `recessiveCounts.zipWithIndex.find` pattern preserves input order so the reported index matches what the caller sees.

**`RecessiveCountOutOfRange(index, value, max)`:** carries the 0-indexed position of the offending element, the offending value, and `max = 2n`. Mirrors the spec-21 `EmptyIntron(index)` and spec-20 `EdgeEndpointOutOfRange(edge, n)` precedents.

### Decision 2: Algorithm — extend WrightFisher with a third method

```scala
def fixationLogProbs(problem: WrightFisherFixationProblem): Vector[Vector[Double]] = {
  val twoN   = 2 * problem.n
  val states = twoN + 1

  // Transition matrix (shared shape with atLeast — but built locally per call).
  val transition: Vector[Vector[Double]] =
    (0 to twoN).toVector.map(i => binomialPmf(twoN, i.toDouble / twoN.toDouble))

  // For each factor j, simulate m generations and record log10(dist(twoN)) at each step.
  // Result indexing: result(g-1)(j) = log10(P(fixated at dominant by gen g, starting from A[j] recessive))
  val perFactorTimeline: Vector[Vector[Double]] =
    problem.recessiveCounts.map { a =>
      val initial: Vector[Double] =
        Vector.tabulate(states)(idx => if (idx == twoN - a) 1.0 else 0.0)
      val (_, logs) = (1 to problem.m).foldLeft((initial, Vector.empty[Double])) {
        case ((dist, acc), _) =>
          val newDist = Vector.tabulate(states) { j =>
            (0 until states).iterator.map(i => dist(i) * transition(i)(j)).sum
          }
          (newDist, acc :+ Math.log10(newDist(twoN)))
      }
      logs
    }

  // perFactorTimeline is k × m (per-factor rows of length m). Transpose to m × k.
  Vector.tabulate(problem.m) { g =>
    Vector.tabulate(problem.recessiveCounts.size) { j =>
      perFactorTimeline(j)(g)
    }
  }
}
```

**Why per-factor inner loop, then transpose:** the natural simulation walks each factor's distribution forward through `m` generations. The output requires the *transposed* layout (rows = generations, columns = factors). Computing per-factor and transposing at the end is clearer than interleaving all factors per generation. At `k ≤ 100, m ≤ 100` the transpose cost is negligible.

**`Math.log10` of `0.0` yields `Double.NegativeInfinity`** — the framework's `RandomMatch.logProbabilities` and `IndependentSegregation.logProbs` already document this as expected behavior. For a starting state of `0` dominant (the all-recessive absorbing state, when `A[j] = 2n`), the fixation probability is `0.0` at every generation, so `log10(0) = -∞`. Tested as an edge case.

**Reuses `binomialPmf`:** the private helper from spec 26 is shared. No duplication.

**Complexity:** `O(m · k · (2n+1)²)` per call. At `n=100, m=100, k=100` this is ~`4 · 10⁹` ops — *would* take seconds but in practice tests use small inputs. Generous bounds are safe.

### Decision 3: Output is `Vector[Vector[Double]]` (m × k, rows = generations)

Matches Rosalind's specified output layout exactly: row `i` of the printed matrix corresponds to generation `i+1`, and column `j` corresponds to factor `j`. No matrix-wrapper type — bare nested `Vector` is the simplest representation that captures the rectangular structure.

**Alternative considered:** introduce a `LogFixationMatrix` wrapper analogous to spec 18's `ProfileMatrix`. Rejected because `ProfileMatrix` has row-named accessors (`.a`, `.c`, `.g`, `.t`) corresponding to the nucleotide ADT; this matrix has no analogous named structure (rows are integer generations, columns are integer factor indices), so a wrapper adds ceremony without information.

### Decision 4: Method name `fixationLogProbs`

- "Fixation" is the genetics term for an allele reaching 100% frequency. In Wright-Fisher, fixation means the population has reached an absorbing state.
- "LogProbs" matches the framework's `RandomMatch.logProbabilities` and `IndependentSegregation.logProbs` convention.
- Concise and descriptive — superior to alternatives like `lossLogProbs` (reverses the perspective) or `founderEffect` (hides what's computed).

### Decision 5: Reuse the existing `WrightFisher` object

Each of the three methods (`atLeast`, `expectedFrequencies`, `fixationLogProbs`) takes a distinct validated input bundle (`WrightFisherProblem`, `WrightFisherExpectationProblem`, `WrightFisherFixationProblem`). The compiler routes calls correctly by type. The `binomialPmf` helper is shared. This is the established pattern from spec 27 — proven to work.

## Risks / Trade-offs

- **Risk:** At the upper bounds (`n=100, m=100, k=100`), the algorithm does ~`4 × 10⁹` floating-point ops. → **Mitigation:** the bounds are deliberately generous; real Rosalind inputs are far smaller. If a performance concern arises, the transposition can be folded into the main loop (rebuilding row-by-row instead of factor-by-factor). The Vector representation is also swappable for `Array[Array[Double]]` if needed.
- **Risk:** `Math.log10(0.0) = NegativeInfinity` could surprise callers expecting a real number. → **Mitigation:** documented in scaladoc; tested as an edge case for `A[j] = 2n`.
- **Trade-off:** Six error cases on the bundle (one per validation stage). → **Mitigation:** matches the framework's existing pattern. Each error name is descriptive (`RecessiveCountOutOfRange` rather than generic `ArrayElementOutOfRange`), so callers can pattern-match on which constraint was violated.
- **Trade-off:** Building the transition matrix once per call (not cached across calls). → **Mitigation:** the cost is `O((2n+1)²)` per call (~`40K` ops at `n=100`), negligible compared to the main `O(m · k · (2n+1)²)` cost. Caching would couple unrelated calls.
- **Risk:** Reusing the `WrightFisher` object as it grows means a single file now hosts three independent methods. → **Mitigation:** at three methods the file is still ~150 lines and each method has its own self-contained body plus shared scaladoc context. If we hit five-plus methods we can reconsider — for now, single-file cohesion wins.
