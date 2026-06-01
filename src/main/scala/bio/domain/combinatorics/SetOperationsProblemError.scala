package bio.domain.combinatorics

/** Construction failures for [[SetOperationsProblem]] (Rosalind SETO). */
sealed trait SetOperationsProblemError
object SetOperationsProblemError {

  /** The supplied universe size was less than 1. The universe `{1, …, n}` is defined
    * for positive integers. Carries the offending value.
    */
  final case class NonPositiveUniverse(value: Int) extends SetOperationsProblemError

  /** The supplied universe size exceeded the per-problem maximum (`max`). Carries the
    * offending value and the maximum so callers need not know the internal constant.
    */
  final case class ExceedsMaximum(value: Int, max: Int) extends SetOperationsProblemError

  /** A subset contained an element outside the universe `{1, …, universe}`.
    *
    * @param setLabel the offending subset (`"A"` or `"B"`)
    * @param value    the out-of-range element
    * @param universe the declared universe size `n`
    */
  final case class ElementOutOfRange(setLabel: String, value: Int, universe: Int)
      extends SetOperationsProblemError
}
