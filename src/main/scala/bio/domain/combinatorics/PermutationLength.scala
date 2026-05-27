package bio.domain.combinatorics

/** A validated input length for the permutations algorithm.
  *
  * Constructable only via [[PermutationLength.from]] which enforces `1 <= value <= 7`
  * (the Rosalind cap; factorial growth makes higher values impractical to enumerate
  * fully — 8! = 40320, 12! = 479001600).
  *
  * Implemented as `sealed abstract case class` to prevent Scala 2.13's case-class
  * `apply` and `copy` synthesis from leaking around the smart constructor.
  */
sealed abstract case class PermutationLength(value: Int)

object PermutationLength {
  private val MaxLength: Int = 7

  def from(value: Int): Either[PermutationLengthError, PermutationLength] =
    if (value < 1)
      Left(PermutationLengthError.NonPositive(value))
    else if (value > MaxLength)
      Left(PermutationLengthError.ExceedsMaximum(value, MaxLength))
    else
      Right(new PermutationLength(value) {})
}
