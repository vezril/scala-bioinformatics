package bio.domain.nucleic

/** Validation errors for [[TransitionTransversionProblem]] (Rosalind TRAN). */
sealed trait TransitionTransversionProblemError

object TransitionTransversionProblemError {

  /** One of the sequences exceeded the maximum allowed length. */
  final case class SequenceTooLong(length: Int, max: Int)
      extends TransitionTransversionProblemError

  /** The two sequences have different lengths. */
  final case class LengthMismatch(firstLength: Int, secondLength: Int)
      extends TransitionTransversionProblemError
}
