package bio.domain.analysis

/** Validation errors for [[RandomMotifProblem]] (Rosalind RSTR). */
sealed trait RandomMotifProblemError

object RandomMotifProblemError {

  /** The motif exceeds the maximum allowed length. */
  final case class MotifTooLong(length: Int, max: Int) extends RandomMotifProblemError

  /** The trial count `N` is not a positive integer. */
  final case class NonPositiveTrials(trials: Int) extends RandomMotifProblemError

  /** The trial count `N` exceeds the maximum allowed. */
  final case class TooManyTrials(trials: Int, max: Int) extends RandomMotifProblemError
}
