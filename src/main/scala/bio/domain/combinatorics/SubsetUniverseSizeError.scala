package bio.domain.combinatorics

/** Construction failures for [[SubsetUniverseSize]]. */
sealed trait SubsetUniverseSizeError
object SubsetUniverseSizeError {

  /** The supplied value was less than 1. Subset counting is defined for positive
    * integers (the universe set has at least one element). Carries the offending value.
    */
  final case class NonPositive(value: Int) extends SubsetUniverseSizeError

  /** The supplied value exceeded the per-problem maximum (`max`). Carries the
    * offending value and the maximum so callers don't need to know the internal
    * constant.
    */
  final case class ExceedsMaximum(value: Int, max: Int) extends SubsetUniverseSizeError
}
