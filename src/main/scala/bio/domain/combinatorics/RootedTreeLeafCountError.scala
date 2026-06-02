package bio.domain.combinatorics

/** Construction failures for [[RootedTreeLeafCount]] (Rosalind ROOT — "Counting Rooted
  * Binary Trees"). Mirrors [[LeafCountError]] (its unrooted CUNR sibling).
  */
sealed trait RootedTreeLeafCountError
object RootedTreeLeafCountError {

  /** The supplied value was less than 1. The number of labeled taxa is a positive
    * integer. Carries the offending value.
    */
  final case class NonPositive(value: Int) extends RootedTreeLeafCountError

  /** The supplied value exceeded the per-problem maximum (`max`). Carries the offending
    * value and the maximum.
    */
  final case class ExceedsMaximum(value: Int, max: Int) extends RootedTreeLeafCountError
}
