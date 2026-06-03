package bio.domain.analysis

/** Validation errors for [[MaxGapProblem]] (Rosalind MGAP). */
sealed trait MaxGapProblemError

object MaxGapProblemError {

  /** One of the input DNA strings exceeds the maximum allowed length. */
  final case class SequenceTooLong(length: Int, max: Int) extends MaxGapProblemError
}
