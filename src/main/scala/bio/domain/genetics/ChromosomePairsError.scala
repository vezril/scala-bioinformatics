package bio.domain.genetics

/** Construction failures for [[ChromosomePairs]]. */
sealed trait ChromosomePairsError
object ChromosomePairsError {

  /** The supplied value was less than 1. Chromosome counts are at least 1 (one
    * chromosome pair = a diploid genome with 2 chromosomes). Carries the offending
    * value.
    */
  final case class NonPositive(value: Int) extends ChromosomePairsError

  /** The supplied value exceeded the per-problem maximum (`max`). Carries the
    * offending value and the maximum so callers don't need to know the internal
    * constant.
    */
  final case class ExceedsMaximum(value: Int, max: Int) extends ChromosomePairsError
}
