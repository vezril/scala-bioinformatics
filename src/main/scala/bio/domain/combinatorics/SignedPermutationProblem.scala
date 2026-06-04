package bio.domain.combinatorics

/** Validated input for the Rosalind SIGN ("Enumerating Oriented Gene Orderings")
  * problem — see
  * [[bio.algorithms.combinatorics.SignedPermutationEnumeration.enumerate]].
  *
  * Wraps the length `n`. The smart constructor validates, first-failure-wins:
  * `n >= 1`, else `NonPositive`; then `n <= 6` (the Rosalind cap — the output
  * grows as `n! * 2^n`), else `ExceedsMaximum`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[SignedPermutationProblem.from]].
  */
sealed abstract case class SignedPermutationProblem(n: Int)

object SignedPermutationProblem {
  private val MaxLength: Int = 6

  def from(n: Int): Either[SignedPermutationProblemError, SignedPermutationProblem] =
    if (n < 1)
      Left(SignedPermutationProblemError.NonPositive(n))
    else if (n > MaxLength)
      Left(SignedPermutationProblemError.ExceedsMaximum(n, MaxLength))
    else
      Right(new SignedPermutationProblem(n) {})
}
