package bio.domain.combinatorics

sealed trait PermutationLengthError
object PermutationLengthError {
  final case class NonPositive(value: Int)                  extends PermutationLengthError
  final case class ExceedsMaximum(value: Int, max: Int)     extends PermutationLengthError
}
