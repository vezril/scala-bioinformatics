package bio.domain.combinatorics

/** The number `n` of labeled taxa of the rooted binary trees being counted by the
  * [[bio.algorithms.combinatorics.RootedBinaryTrees]] algorithm (Rosalind ROOT —
  * "Counting Rooted Binary Trees"). The rooted counterpart of [[LeafCount]] (CUNR).
  *
  * Constructable only via [[RootedTreeLeafCount.from]] which enforces:
  *   - `value >= 1` (Rosalind problem: "positive integer")
  *   - `value <= 1000` (Rosalind upper bound)
  *
  * Validation order: lower bound, then upper bound. First failure wins.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class RootedTreeLeafCount(value: Int)

object RootedTreeLeafCount {
  private val MaxN: Int = 1000

  def from(value: Int): Either[RootedTreeLeafCountError, RootedTreeLeafCount] =
    if (value < 1) Left(RootedTreeLeafCountError.NonPositive(value))
    else if (value > MaxN) Left(RootedTreeLeafCountError.ExceedsMaximum(value, MaxN))
    else Right(new RootedTreeLeafCount(value) {})
}
