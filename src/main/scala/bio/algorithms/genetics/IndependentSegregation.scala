package bio.algorithms.genetics

import bio.domain.genetics.ChromosomePairs

/** Computes, for each `k = 1..2n`, the common logarithm of the probability that two
  * diploid siblings share at least `k` of their `2n` chromosomes — i.e.
  * `log10(P(X ≥ k))` where `X ~ Bin(2n, 1/2)` (Rosalind INDC).
  *
  * Each chromosome is independently inherited from either of the two parental copies
  * with probability `1/2`; siblings sample independently, so the count of shared
  * chromosomes is a sum of `2n` independent `Bernoulli(1/2)` trials — a `Bin(2n, 1/2)`.
  *
  * **Algorithm:**
  *   1. Build the PMF row `pmf(k) = C(2n, k) / 2^(2n)` for `k = 0..2n` via the
  *      multiplicative recurrence `pmf(k+1) = pmf(k) × (2n - k) / (k + 1)`, starting
  *      from `pmf(0) = (1/2)^(2n)`. Avoids computing the explosive `C(2n, k)` (which
  *      reaches ~`1e29` at `n = 50`) by staying in normalized `Double` range
  *      throughout.
  *   2. Compute upper-tail sums `tail(k) = Σ_{j=k}^{2n} pmf(j)` for `k = 1..2n` by
  *      sweeping right-to-left, accumulating the smallest masses first. Avoids the
  *      catastrophic cancellation that `1 − P(X < k)` would introduce for small upper
  *      tails.
  *   3. Take `Math.log10(tail(k))` for `k = 1..2n` and return as a `Vector[Double]`
  *      of length `2n`.
  *
  * **Numerical notes:**
  *   - `Math.pow(0.5, 2n.toDouble)` is exact for integer exponents; `0.5^100 ≈
  *     7.89 × 10^-31`, comfortably above `Double`'s underflow threshold (~`2.2e-308`).
  *   - The right-to-left tail sweep uses a local `Array[Double]` for the running
  *     accumulator; the imperative inner loop is the clearest expression of the
  *     algorithm, and the array never escapes the function.
  *   - If a tail value were to underflow to `0.0`, `Math.log10(0.0) =
  *     Double.NegativeInfinity` — the function remains total (matches the behavior of
  *     [[bio.algorithms.analysis.RandomMatch.logProbabilities]]).
  *
  * **Complexity:** `O(n)` time and `O(n)` memory. At `n ≤ 50` (so `2n ≤ 100`), all
  * loops are ≤ 100 iterations — trivially fast.
  */
object IndependentSegregation {

  def logProbs(pairs: ChromosomePairs): Vector[Double] = {
    val twoN = 2 * pairs.value

    // Step 1: PMF row via multiplicative recurrence.
    val pmf: Vector[Double] =
      (0 until twoN).foldLeft(Vector(Math.pow(0.5, twoN.toDouble))) { (acc, k) =>
        val next = acc.last * (twoN - k).toDouble / (k + 1).toDouble
        acc :+ next
      }

    // Step 2: upper-tail sums via right-to-left sweep. tail(k) = P(X >= k) for k=1..2n.
    val tail = Array.ofDim[Double](twoN + 1)
    var acc  = 0.0
    var k    = twoN
    while (k >= 1) {
      acc += pmf(k)
      tail(k) = acc
      k -= 1
    }

    // Step 3: log10 of each tail probability, in the order k=1..2n.
    (1 to twoN).toVector.map(j => Math.log10(tail(j)))
  }
}
