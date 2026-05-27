package bio.domain.genetics

/** Construction failures for [[WrightFisherExpectationProblem]]. */
sealed trait WrightFisherExpectationProblemError
object WrightFisherExpectationProblemError {

  /** The supplied `n` was less than 1. Carries the offending value. */
  final case class NonPositiveN(value: Int) extends WrightFisherExpectationProblemError

  /** The supplied `n` exceeded the per-problem maximum (`max`). Carries the
    * offending value and the maximum so callers don't need to know the internal
    * constant.
    */
  final case class NExceedsMaximum(value: Int, max: Int) extends WrightFisherExpectationProblemError

  /** The supplied `p` vector had more elements than the per-problem maximum (`max`).
    * Named `TooManyProbabilities` rather than generic `ExceedsMaximum` to signal the
    * constraint's meaning — mirrors the descriptive style used by
    * [[WrightFisherProblemError.MExceedsTotalAlleles]] in the wright-fisher-genetic-drift
    * capability.
    */
  final case class TooManyProbabilities(size: Int, max: Int)
      extends WrightFisherExpectationProblemError
}
