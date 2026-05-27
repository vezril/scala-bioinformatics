## Context

Spec 24 computes `log10(P(X ≥ k))` for every `k = 1..2n` where `X ~ Bin(2n, 1/2)`. Output: a `Vector[Double]` of length `2n`. The model is independent assortment of `2n` chromosomes between diploid siblings (each chromosome inherited from one of two parental copies with probability 1/2; siblings independently sample).

The framework already has three genetics-flavored probability algorithms in `bio.algorithms.genetics`:

- `MendelianInheritance.dominantProbability(population): Probability` — single-genotype probability.
- `ExpectedOffspring.expected(couples): Double` — expected count.
- `IndependentAlleles.atLeast(problem): Probability` — single tail probability via a binomial PMF recurrence + sum.

This spec is closest in spirit to `IndependentAlleles`: both apply Mendel's law of independent assortment under a `Bin(n, 1/2)`-style model. The difference is that `IndependentAlleles` returns a *single* tail probability for one specific threshold, while this spec returns *every* tail log-probability for `k = 1..2n`.

## Goals / Non-Goals

**Goals:**
- `ChromosomePairs` validated wrapper in `bio.domain.genetics` enforcing `1 ≤ n ≤ 50`.
- `ChromosomePairsError` ADT with `NonPositive` and `ExceedsMaximum`.
- `IndependentSegregation.logProbs(pairs): Vector[Double]` — total over the validated input; result length is exactly `2 * pairs.value`.

**Non-Goals:**
- Recombination modeling. Rosalind explicitly says "we do not consider recombination for now" — chromosomes are perfectly independent.
- A separate algorithm for `P(X = k)` (lower-tail point probabilities). Not asked for; the upper-tail cumulative is the canonical INDC output.
- Re-implementing `IndependentAlleles`. The PMF recurrence is similar in shape but the *result* shape differs (scalar vs. vector) and the *input* differs (`(k, N)` two-integer bundle vs. a single `n`). Keeping the two algorithms separate matches Rosalind's two distinct problem definitions.

## Decisions

### Decision 1: `ChromosomePairs` value-type wrapper (not a bundle)

```scala
sealed abstract case class ChromosomePairs(value: Int)
object ChromosomePairs {
  private val MaxN: Int = 50
  def from(value: Int): Either[ChromosomePairsError, ChromosomePairs] =
    if (value < 1) Left(ChromosomePairsError.NonPositive(value))
    else if (value > MaxN) Left(ChromosomePairsError.ExceedsMaximum(value, MaxN))
    else Right(new ChromosomePairs(value) {})
}
```

Single integer input, no cross-constraints → use the value-type wrapper pattern (mirrors `PermutationLength`, `SubsetUniverseSize`, `OverlapLength`). Bundles are for multi-input algorithms or single-input algorithms with cross-constraints; neither applies.

**Name:** `ChromosomePairs` reflects the biological meaning. The integer `n` is the count of *pairs* of chromosomes (haploid count); the total chromosome count being modeled is `2n`. Calling the type `n` or `GenerationSize` would lose this context. The algorithm's scaladoc will reiterate that the binomial has `2 * pairs.value` trials.

**Validation order:** lower bound, then upper bound. First-failure-wins.

### Decision 2: PMF recurrence → upper-tail accumulation → element-wise `log10`

```scala
def logProbs(pairs: ChromosomePairs): Vector[Double] = {
  val twoN = 2 * pairs.value
  // PMF: pmf(k) = C(2n, k) / 2^(2n)
  // Recurrence: pmf(k+1) = pmf(k) * (2n - k) / (k + 1)
  // Start: pmf(0) = 0.5^(2n)
  val pmf: Vector[Double] =
    (0 until twoN).foldLeft(Vector(Math.pow(0.5, twoN.toDouble))) { (acc, k) =>
      val next = acc.last * (twoN - k).toDouble / (k + 1).toDouble
      acc :+ next
    }
  // tail(k) = P(X >= k) for k = 1..2n. Sweep right-to-left.
  val tail = Array.ofDim[Double](twoN + 1)  // tail(2n+1) is implicitly 0 (acc starts at 0)
  var acc  = 0.0
  var k    = twoN
  while (k >= 1) {
    acc += pmf(k)
    tail(k) = acc
    k -= 1
  }
  (1 to twoN).toVector.map(k => Math.log10(tail(k)))
}
```

**Why PMF recurrence:** computing `C(2n, k)` directly would explode (`C(100, 50) ≈ 1.0e29`) before dividing by `2^(2n)`. The multiplicative recurrence stays in normalized `Double` range throughout: at `n = 50`, the smallest term is `pmf(0) = 0.5^100 ≈ 7.89 × 10^-31`, comfortably above `Double`'s underflow threshold (`~2.2 × 10^-308`).

**Why upper-tail sweep right-to-left:** `P(X ≥ k)` is most stable computed as a running sum from `k = 2n` downward (smallest mass first). Right-to-left avoids the catastrophic cancellation that `1 − P(X < k)` would introduce for small upper tails.

**Why `Array` for the running accumulator:** the tail accumulation is a simple inner loop with mutable state; an `Array[Double]` filled imperatively is the cleanest expression. The PMF is built immutably as a `Vector[Double]` and the final result is `Vector[Double]`. The mutable array is local to the function — no leak.

**`Math.log10` on `0.0`:** if a tail value underflows to exactly `0.0` (won't happen at `n ≤ 50` but defensively noted), `log10(0)` produces `Double.NegativeInfinity`. The function remains total — same behavior as `RandomMatch.logProbabilities`.

### Decision 3: Output is a bare `Vector[Double]`

Mirrors `RandomMatch.logProbabilities` (the existing log-probability vector algorithm). No `LogProbability` wrapper. Length is exactly `2 * pairs.value`, documented in scaladoc and verified by an explicit test.

### Decision 4: Naming and placement

- **Object:** `IndependentSegregation` — direct echo of the Rosalind problem name ("Independent Segregation of Chromosomes"), parallel to `IndependentAlleles` in the same subdomain.
- **Method:** `logProbs(pairs)` — descriptive without being overlong. The "log" prefix marks the output as log-scale (vs. raw probabilities).
- **Subdomain:** `bio.{algorithms,domain}.genetics` — joins the three existing genetics algorithms. The shared biological theme (Mendel's law applied to inheritance scenarios) outweighs the math-flavored alternative placement in `stats`.

## Risks / Trade-offs

- **Risk:** `Array` use deviates from the otherwise-immutable framework style. → **Mitigation:** the array is local to the function, never escapes, and the alternative (Vector update with `.updated`) is `O(n)` per update vs. `O(1)` for `Array.update`. The inner loop runs `2n ≤ 100` times so even the slower form would be fine, but the imperative inner loop is the most readable expression of "right-to-left running sum".
- **Trade-off:** `Math.pow(0.5, twoN.toDouble)` for the PMF base case. → **Mitigation:** `Math.pow(0.5, n)` is exact for integer `n` since `0.5` is `2^-1` and the result is `2^-n`, exactly representable as a `Double` for `n ≤ 1024`.
- **Trade-off:** `Vector :+ next` per PMF step is O(1) amortized for `Vector`, but allocates per step. → **Mitigation:** at `2n ≤ 100` iterations the allocation cost is microseconds. Builders / array-based PMF would be premature optimization.
- **Risk:** A future caller might want the raw probabilities (not log10) or the PMF row. → **Mitigation:** if that need arises we can add `IndependentSegregation.probs` or `IndependentSegregation.pmf` as separate methods. The current scope matches Rosalind's INDC exactly.
