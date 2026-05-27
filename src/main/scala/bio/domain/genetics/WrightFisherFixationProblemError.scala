package bio.domain.genetics

/** Construction failures for [[WrightFisherFixationProblem]]. */
sealed trait WrightFisherFixationProblemError
object WrightFisherFixationProblemError {

  /** The supplied `n` was less than 1. */
  final case class NonPositiveN(value: Int) extends WrightFisherFixationProblemError

  /** The supplied `n` exceeded the per-problem maximum. */
  final case class NExceedsMaximum(value: Int, max: Int) extends WrightFisherFixationProblemError

  /** The supplied `m` was less than 1. */
  final case class NonPositiveM(value: Int) extends WrightFisherFixationProblemError

  /** The supplied `m` exceeded the per-problem maximum. */
  final case class MExceedsMaximum(value: Int, max: Int) extends WrightFisherFixationProblemError

  /** The supplied `recessiveCounts` vector had more elements than the per-problem
    * maximum.
    */
  final case class TooManyRecessiveCounts(size: Int, max: Int)
      extends WrightFisherFixationProblemError

  /** A `recessiveCounts(index)` value was outside the allowed range `[0, 2n]`.
    * Carries the 0-indexed position of the first offending element, its value, and
    * the computed maximum `2 * n`.
    */
  final case class RecessiveCountOutOfRange(index: Int, value: Int, max: Int)
      extends WrightFisherFixationProblemError
}
