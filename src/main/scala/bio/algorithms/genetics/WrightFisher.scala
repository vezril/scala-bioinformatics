package bio.algorithms.genetics

import bio.domain.genetics.{WrightFisherExpectationProblem, WrightFisherProblem}
import bio.domain.stats.Probability

/** Wright-Fisher genetic-drift tail probability (Rosalind WFMD).
  *
  * Models the count of dominant alleles in a diploid population of `n` individuals
  * (`2n` chromosomes) as a Markov chain on the state space `S = {0, 1, ..., 2n}`.
  * Each generation re-samples all `2n` chromosomes independently: a dominant allele
  * is chosen with probability `p = d / (2n)` (where `d` is the current dominant
  * count), so the next state follows `Bin(2n, p)`.
  *
  * `atLeast(problem)` computes the probability that, after `problem.g` generations
  * starting from `problem.m` dominant alleles, at least `problem.k` recessive alleles
  * are present. Equivalent to "at most `2n − k` dominant alleles".
  *
  * **Algorithm:**
  *   1. Build the transition matrix `T[i][j] = Bin(2n, i/(2n)).pmf(j)` for every
  *      pair of states `(i, j) ∈ S²`. The PMF row is computed by the multiplicative
  *      recurrence `pmf(0) = (1 − p)^(2n)`, `pmf(k+1) = pmf(k) · (2n − k)/(k+1) · p/(1 − p)`,
  *      with special cases at `p == 0` (one-hot at 0 — all-recessive absorbing state)
  *      and `p == 1` (one-hot at `2n` — all-dominant absorbing state).
  *   2. Start from a one-hot distribution at state `m`.
  *   3. Apply `T` exactly `g` times via vector-by-matrix multiplication
  *      `dist_new(j) = Σ_i dist_old(i) * T(i)(j)`.
  *   4. Sum the resulting distribution over states `[0, 2n − k]` (at most `2n − k`
  *      dominant ⇔ at least `k` recessive). Wrap in `Probability.unsafeFrom`.
  *
  * **`Probability.unsafeFrom` justification:** the final distribution is a probability
  * distribution (its entries are non-negative and sum to 1, by construction). Any
  * partial sum over a contiguous range is therefore in `[0, 1]` — a valid probability.
  * Floating-point accumulation error at this scale (`states ≤ 15`, `g ≤ 6`) is bounded
  * by ~`10^-13`, well within `Double`'s slack.
  *
  * **Complexity:** `O(g · (2n+1)²)` floating-point multiplies. At the upper bound
  * `n = 7, g = 6` that's `6 × 225 = 1350` multiplications — microseconds per call.
  */
object WrightFisher {

  def atLeast(problem: WrightFisherProblem): Probability = {
    val twoN   = 2 * problem.n
    val states = twoN + 1

    // Step 1: transition matrix. T(i)(j) = P(j dominant next gen | i dominant this gen).
    val transition: Vector[Vector[Double]] =
      (0 to twoN).toVector.map { i =>
        binomialPmf(twoN, i.toDouble / twoN.toDouble)
      }

    // Step 2: initial one-hot distribution at state m.
    val initial: Vector[Double] =
      Vector.tabulate(states)(j => if (j == problem.m) 1.0 else 0.0)

    // Step 3: apply transition g times.
    val finalDist = (1 to problem.g).foldLeft(initial) { (dist, _) =>
      Vector.tabulate(states) { j =>
        (0 until states).iterator.map(i => dist(i) * transition(i)(j)).sum
      }
    }

    // Step 4: tail-sum over "at most (2n − k) dominant".
    val threshold = twoN - problem.k
    val tail      = (0 to threshold).iterator.map(finalDist(_)).sum

    Probability.unsafeFrom(tail)
  }

  /** Expected value of `Bin(n, p)` for each `p` in the input vector (Rosalind EBIN).
    *
    * Returns a `Vector[Double]` of the same length as `problem.p` where each element
    * is `problem.n.toDouble * prob.value` for the corresponding probability — the
    * textbook identity `E[Bin(n, p)] = n · p`.
    *
    * **Output type:** bare `Vector[Double]` rather than `Vector[Probability]`. The
    * expected count is a *count*, not a probability — it can exceed 1 (the Rosalind
    * sample's `5.1` does). Mirrors the bare-`Vector[Double]` output choices in
    * [[bio.algorithms.analysis.RandomMatch.logProbabilities]] and
    * [[IndependentSegregation.logProbs]].
    *
    * **Shares the `WrightFisher` object** with [[atLeast]] but no state — the two
    * methods are independently usable and each takes its own validated input bundle
    * ([[WrightFisherProblem]] for `atLeast`, [[WrightFisherExpectationProblem]]
    * here). The compiler routes calls correctly by type.
    *
    * **Complexity:** `O(m)` floating-point multiplications where `m = problem.p.size`.
    * At `m ≤ 20` this is microseconds per call.
    */
  def expectedFrequencies(problem: WrightFisherExpectationProblem): Vector[Double] =
    problem.p.map(prob => problem.n.toDouble * prob.value)

  /** Binomial PMF row of length `n + 1` at success probability `p`, computed via the
    * multiplicative recurrence. Special-cases the absorbing extremes:
    *   - `p == 0.0` → one-hot at index 0 (zero successes with probability 1).
    *   - `p == 1.0` → one-hot at index `n` (all successes with probability 1).
    *
    * For `p ∈ (0, 1)`: `pmf(0) = (1 − p)^n`, `pmf(k+1) = pmf(k) · (n − k)/(k+1) · p/(1 − p)`.
    */
  private def binomialPmf(n: Int, p: Double): Vector[Double] =
    if (p == 0.0) Vector.tabulate(n + 1)(k => if (k == 0) 1.0 else 0.0)
    else if (p == 1.0) Vector.tabulate(n + 1)(k => if (k == n) 1.0 else 0.0)
    else {
      val base  = Math.pow(1.0 - p, n.toDouble)
      val ratio = p / (1.0 - p)
      (0 until n).foldLeft(Vector(base)) { (acc, k) =>
        val next = acc.last * (n - k).toDouble / (k + 1).toDouble * ratio
        acc :+ next
      }
    }
}
