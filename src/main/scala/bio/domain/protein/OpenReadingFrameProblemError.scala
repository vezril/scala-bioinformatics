package bio.domain.protein

/** Validation errors for [[OpenReadingFrameProblem]] (Rosalind ORF). */
sealed trait OpenReadingFrameProblemError

object OpenReadingFrameProblemError {

  /** The input DNA string exceeds the maximum allowed length. */
  final case class SequenceTooLong(length: Int, max: Int)
      extends OpenReadingFrameProblemError
}
