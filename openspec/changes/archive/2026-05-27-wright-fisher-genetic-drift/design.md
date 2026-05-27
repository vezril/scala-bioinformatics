## Context

Spec 26 models genetic drift via Wright-Fisher: a Markov chain on the state space `S = {0, 1, ..., 2N}` representing the number of dominant alleles in a diploid population of `N` individuals (`2N` total chromosomes). Each generation:

- Compute `p = d / (2N)` where `d` is the current dominant-allele count.
- Sample `2N` new alleles independently, each dominant with probability `p` and recessive with probability `1 − p`.
- The new state is the count of dominant alleles, so `d' ~ Bin(2N, p)`.

The transition kernel is therefore `T[d][d'] = C(2N, d') · (d/2N)^d' · (1 − d/2N)^(2N − d')`. Starting from a one-hot distribution at state `m`, applying `T` exactly `g` times yields the post-`g`-generation distribution. The answer is `Σ_{d' = 0}^{2N − k} dist[d']` (the probability of having at most `2N − k` dominant alleles, equivalently at least `k` recessive).

The framework already has `Probability` in `bio.domain.stats` (validated `Double` in `[0, 1]`).

## Goals / Non-Goals

**Goals:**
- `WrightFisherProblem` validated bundle in `bio.domain.genetics` (four integer parameters with bounds and two cross-constraints).
- `WrightFisherProblemError` ADT with 8 cases.
- `WrightFisher.atLeast(problem): Probability` — total over the validated input, returns a single `Probability`.
- Match Rosalind's sample (`N=4, m=6, g=2, k=1 → ≈ 0.772`) within absolute error `< 0.001`.

**Non-Goals:**
- Full distribution exposure. Out of scope; only the tail-probability is asked. If a future caller needs the full distribution we'd add `WrightFisher.distribution(...)` separately.
- Memoization of transition matrices across calls. Each call rebuilds T from scratch. At `(2N+1) ≤ 15` states the matrix is tiny.
- Generalizing to non-Wright-Fisher drift models (Moran model, etc.). Out of scope.

## Decisions

### Decision 1: Four-parameter bundle with 8 validation cases

```scala
sealed abstract case class WrightFisherProblem(n: Int, m: Int, g: Int, k: Int)
object WrightFisherProblem {
  private val MaxN: Int = 7
  private val MaxG: Int = 6

  def from(n: Int, m: Int, g: Int, k: Int): Either[WrightFisherProblemError, WrightFisherProblem] = {
    if (n < 1)        Left(WrightFisherProblemError.NonPositiveN(n))
    else if (n > MaxN) Left(WrightFisherProblemError.NExceedsMaximum(n, MaxN))
    else if (m < 1)   Left(WrightFisherProblemError.NonPositiveM(m))
    else if (m > 2 * n) Left(WrightFisherProblemError.MExceedsTotalAlleles(m, 2 * n))
    else if (g < 1)   Left(WrightFisherProblemError.NonPositiveG(g))
    else if (g > MaxG) Left(WrightFisherProblemError.GExceedsMaximum(g, MaxG))
    else if (k < 1)   Left(WrightFisherProblemError.NonPositiveK(k))
    else if (k > 2 * n) Left(WrightFisherProblemError.KExceedsTotalAlleles(k, 2 * n))
    else Right(new WrightFisherProblem(n, m, g, k) {})
  }
}
```

**Validation order:** `n` lower → `n` upper → `m` lower → `m` upper (cross-constraint on `n`) → `g` lower → `g` upper → `k` lower → `k` upper (cross-constraint on `n`). First failure wins.

`n` must be validated first because `m`'s upper bound and `k`'s upper bound both depend on `2n`. Without a valid `n` we can't compute the cross-constraint.

**8 error cases — not 4:** each of the four integers gets its own pair (`NonPositiveX`, `XExceedsMaximum` or `XExceedsTotalAlleles`). The four `NonPositive*` cases share the same shape (`value: Int`) but are distinct case classes so callers can pattern-match on which input was invalid. Same precedent as `PartialPermutationProblem` (which has `NonPositiveN`, `NonPositiveK` as separate cases despite identical structure).

**`MExceedsTotalAlleles` / `KExceedsTotalAlleles`:** these names emphasize the cross-constraint meaning ("can't have more dominant alleles than total alleles") rather than a generic `ExceedsMaximum`. The error carries both `value` and the computed `max = 2 * n` so callers can report the constraint clearly.

### Decision 2: Algorithm — explicit transition kernel + vector-matrix multiply repeated `g` times

```scala
def atLeast(problem: WrightFisherProblem): Probability = {
  val twoN  = 2 * problem.n
  val states = twoN + 1

  // Build transition matrix T (states × states). T(i)(j) = P(j dominant next gen | i dominant this gen).
  val T: Vector[Vector[Double]] =
    (0 to twoN).toVector.map { i =>
      val p = i.toDouble / twoN.toDouble
      binomialPmf(twoN, p)  // length = states
    }

  // Start state: one-hot at m.
  val initial: Vector[Double] = Vector.tabulate(states)(j => if (j == problem.m) 1.0 else 0.0)

  // Apply T g times: dist_new(j) = Σ_i dist_old(i) * T(i)(j).
  val finalDist = (1 to problem.g).foldLeft(initial) { (dist, _) =>
    Vector.tabulate(states) { j =>
      (0 until states).iterator.map(i => dist(i) * T(i)(j)).sum
    }
  }

  // At-least-k-recessive ≡ at-most-(2N-k)-dominant; sum mass over [0, 2N-k].
  val threshold = twoN - problem.k
  val tail = (0 to threshold).iterator.map(finalDist(_)).sum
  Probability.unsafeFrom(tail)
}

private def binomialPmf(n: Int, p: Double): Vector[Double] = {
  // Multiplicative recurrence: pmf(0) = (1-p)^n, pmf(k+1) = pmf(k) * (n-k)/(k+1) * p/(1-p).
  // Special-case p == 0 and p == 1 to avoid division by zero in p/(1-p).
  if (p == 0.0)      Vector.tabulate(n + 1)(k => if (k == 0) 1.0 else 0.0)
  else if (p == 1.0) Vector.tabulate(n + 1)(k => if (k == n) 1.0 else 0.0)
  else {
    val base = Math.pow(1.0 - p, n.toDouble)
    val ratio = p / (1.0 - p)
    (0 until n).foldLeft(Vector(base)) { (acc, k) =>
      val next = acc.last * (n - k).toDouble / (k + 1).toDouble * ratio
      acc :+ next
    }
  }
}
```

**Why explicit transition matrix:** at `2N+1 ≤ 15` states, the matrix is tiny (`15 × 15 = 225` Doubles). Building it once per call is far simpler than computing `T(i)(j)` on-the-fly inside the inner loop. The total work is `g × states² ≤ 6 × 225 = 1350` multiplications per call — microseconds.

**Why multiplicative PMF recurrence:** same idiom as `IndependentSegregation` and `IndependentAlleles`. Avoids computing `C(2N, j)` directly (which is `15` for `2N = 14` — small but still nice to avoid). At each step the value stays in `Double` precision; the smallest value is `(1-p)^(2N) ≥ 0` (`p ≤ 1` always).

**`p == 0` and `p == 1` special cases:** when the current state is `0` (no dominant alleles), `p = 0` and `p/(1-p) = 0/1 = 0` — that's fine arithmetically, but the recurrence's `pmf(k+1) = pmf(k) * ratio * (n-k)/(k+1)` propagates `0` correctly only if we don't compute `0/0`. To be safe, we special-case `p == 0` → PMF is one-hot at 0; `p == 1` → one-hot at `n`. These represent **absorbing states** in the Markov chain — once all alleles are recessive (state 0) or all dominant (state 2N), the population stays there forever.

**Why `Vector` not `Array`:** the entire framework's algorithmic style is immutable. The transition matrix is `Vector[Vector[Double]]`; each generation produces a new `Vector[Double]` for the distribution. The cost at `states ≤ 15` is negligible.

### Decision 3: Output is `Probability` (not bare `Double`)

The tail-sum is structurally a valid probability — it's a sum of probabilities all in `[0, 1]`, and since the PMF sums to 1 the partial sum is in `[0, 1]`. `Probability.unsafeFrom` is the right tool. Mirrors `IndependentAlleles.atLeast` which also returns `Probability`.

### Decision 4: Algorithm name `WrightFisher.atLeast`

- Object: `WrightFisher` — names the model. Distinct from `IndependentAlleles`, `IndependentSegregation`, `DiseaseCarriers` (all of which name biological concepts or events). The Wright-Fisher *model* is the named entity here.
- Method: `atLeast` — short, mirrors `IndependentAlleles.atLeast`. The "what at least?" detail (i.e., `k` recessive copies after `g` generations) is encoded in the problem bundle's name, not the method.

**Alternative considered:** `WrightFisherDrift` for the object, `atLeastRecessive` for the method. Slightly more descriptive but verbose; the bundle name `WrightFisherProblem` already disambiguates and the method name `atLeast` is consistent with the framework's existing tail-probability conventions.

## Risks / Trade-offs

- **Risk:** Floating-point accumulation across `g = 6` iterations could drift. → **Mitigation:** at `2N+1 ≤ 15` states with `g ≤ 6` and `Double` precision (~16 significant digits), the worst-case absolute error is ~`6 × 15 × 10^-16 ≈ 10^-13` — vastly tighter than Rosalind's 0.001 tolerance.
- **Risk:** Tail sum slightly exceeds `1.0` due to floating-point round-off, breaking `Probability.unsafeFrom`'s implicit precondition. → **Mitigation:** `Probability.unsafeFrom` bypasses validation by design; in practice the accumulated error is `~10^-13` which is well within `Double`'s representation slack and never crosses `1.0` for a sub-distribution sum.
- **Trade-off:** 8 error cases is the framework's largest error ADT. → **Mitigation:** each case is justified — there are four integer parameters each needing two-sided bounds, and the cross-constraints have descriptive names (`MExceedsTotalAlleles`) that signal their meaning. Same pattern (per-input case classes) used by `PartialPermutationProblem` and `TreeCompletionProblem`.
- **Trade-off:** Building `Vector[Vector[Double]]` once per call rather than caching across calls. → **Mitigation:** at `states ≤ 15` the allocation cost is sub-microsecond. Caching would couple unrelated calls and complicate the API.
- **Risk:** The `p == 0` / `p == 1` special cases in `binomialPmf` are easy to mis-handle. → **Mitigation:** tested explicitly (`m = 2N` with `g = 1, k = 1` → should be `0.0` because no recessive can ever appear from an all-dominant absorbing state).
