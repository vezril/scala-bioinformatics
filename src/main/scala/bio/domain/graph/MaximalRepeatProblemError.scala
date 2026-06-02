package bio.domain.graph

/** Validation errors for [[MaximalRepeatProblem]] (Rosalind MREP). */
sealed trait MaximalRepeatProblemError

object MaximalRepeatProblemError {

  /** The input DNA string exceeds the maximum allowed length. */
  final case class SequenceTooLong(length: Int, max: Int) extends MaximalRepeatProblemError

  /** The minimum repeat length is not a positive integer. */
  final case class NonPositiveMinLength(minLength: Int) extends MaximalRepeatProblemError
}
