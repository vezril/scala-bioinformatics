package bio.domain.combinatorics

/** Construction failures for [[LeafCount]] (Rosalind CUNR — "Counting Unrooted
  * Binary Trees").
  */
sealed trait LeafCountError
object LeafCountError {

  /** The supplied value was less than 1. The number of labeled leaves is a
    * positive integer. Carries the offending value.
    */
  final case class NonPositive(value: Int) extends LeafCountError

  /** The supplied value exceeded the per-problem maximum (`max`). Carries the
    * offending value and the maximum so callers don't need to know the internal
    * constant.
    */
  final case class ExceedsMaximum(value: Int, max: Int) extends LeafCountError
}
