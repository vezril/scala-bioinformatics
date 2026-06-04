package bio.domain.combinatorics

/** Construction failures for [[SignedPermutationProblem]] (Rosalind SIGN). */
sealed trait SignedPermutationProblemError
object SignedPermutationProblemError {

  /** The requested length was not positive. */
  final case class NonPositive(n: Int) extends SignedPermutationProblemError

  /** The requested length exceeded the Rosalind SIGN cap of 6. */
  final case class ExceedsMaximum(n: Int, max: Int) extends SignedPermutationProblemError
}
