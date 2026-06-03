package bio.domain.combinatorics

/** Validation errors for [[Permutation]] (Rosalind LGIS). */
sealed trait PermutationError

object PermutationError {

  /** The sequence exceeded the maximum allowed length. */
  final case class TooLong(length: Int, max: Int) extends PermutationError

  /** The sequence is not a permutation of `{1, …, length}`. */
  final case class NotAPermutation(values: Vector[Int]) extends PermutationError
}
