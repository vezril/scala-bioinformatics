package bio.domain.analysis

/** Validation errors for [[SupersequenceProblem]] (Rosalind SCSP). */
sealed trait SupersequenceProblemError

object SupersequenceProblemError {

  /** One of the input DNA strings exceeds the maximum allowed length. */
  final case class SequenceTooLong(length: Int, max: Int) extends SupersequenceProblemError
}
