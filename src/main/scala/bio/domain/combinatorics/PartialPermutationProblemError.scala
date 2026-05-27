package bio.domain.combinatorics

sealed trait PartialPermutationProblemError
object PartialPermutationProblemError {
  final case class NonPositiveN(value: Int)                  extends PartialPermutationProblemError
  final case class NExceedsMaximum(value: Int, max: Int)     extends PartialPermutationProblemError
  final case class NonPositiveK(value: Int)                  extends PartialPermutationProblemError
  final case class KExceedsMaximum(value: Int, max: Int)     extends PartialPermutationProblemError
  final case class KExceedsN(k: Int, n: Int)                 extends PartialPermutationProblemError
}
