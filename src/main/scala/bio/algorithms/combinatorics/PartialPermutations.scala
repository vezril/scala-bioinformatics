package bio.algorithms.combinatorics

import bio.domain.combinatorics.PartialPermutationProblem

/** Counts partial permutations: `P(n, k) = n × (n-1) × ... × (n-k+1)` modulo
  * `1,000,000`.
  *
  * Computed via an incremental product with per-step modulo. `Int` arithmetic is
  * sufficient: after each modulo, `acc ∈ [0, 999_999]`, and the largest factor is
  * `n ≤ 100`, so the worst intermediate is `999_999 × 100 = 99_999_900`, well within
  * `Int.MaxValue ≈ 2.15 × 10^9`.
  */
object PartialPermutations {

  private val Modulus: Int = 1_000_000

  def count(problem: PartialPermutationProblem): Int =
    (0 until problem.k).foldLeft(1) { (acc, i) =>
      (acc * (problem.n - i)) % Modulus
    }
}
