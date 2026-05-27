package bio.domain.combinatorics

/** Parameters for the partial-permutations algorithm (the falling factorial `P(n, k)`).
  *
  * Constructable only via [[PartialPermutationProblem.from]] which enforces:
  *   - `1 <= n <= 100`
  *   - `1 <= k <= 10`
  *   - `k <= n` (cross-constraint — selecting `k` items from `n` requires `k ≤ n`)
  *
  * Validation order: `n` lower bound, `n` upper bound, `k` lower bound, `k` upper bound,
  * `k <= n` cross-constraint. First failure wins.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class PartialPermutationProblem(n: Int, k: Int)

object PartialPermutationProblem {
  private val MaxN: Int = 100
  private val MaxK: Int = 10

  def from(
      n: Int,
      k: Int
  ): Either[PartialPermutationProblemError, PartialPermutationProblem] =
    if (n < 1) Left(PartialPermutationProblemError.NonPositiveN(n))
    else if (n > MaxN) Left(PartialPermutationProblemError.NExceedsMaximum(n, MaxN))
    else if (k < 1) Left(PartialPermutationProblemError.NonPositiveK(k))
    else if (k > MaxK) Left(PartialPermutationProblemError.KExceedsMaximum(k, MaxK))
    else if (k > n) Left(PartialPermutationProblemError.KExceedsN(k, n))
    else Right(new PartialPermutationProblem(n, k) {})
}
