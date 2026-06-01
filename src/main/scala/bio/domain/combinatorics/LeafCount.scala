package bio.domain.combinatorics

/** The number `n` of labeled leaves of the unrooted binary trees being counted
  * by the [[bio.algorithms.combinatorics.UnrootedBinaryTrees]] algorithm
  * (Rosalind CUNR).
  *
  * Constructable only via [[LeafCount.from]] which enforces:
  *   - `value >= 1` (Rosalind problem: "positive integer")
  *   - `value <= 1000` (Rosalind upper bound)
  *
  * Validation order: lower bound, then upper bound. First failure wins.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor. Mirrors the framework's
  * existing single-parameter validated wrappers (`SubsetUniverseSize`,
  * `PermutationLength`).
  */
sealed abstract case class LeafCount(value: Int)

object LeafCount {
  private val MaxN: Int = 1000

  def from(value: Int): Either[LeafCountError, LeafCount] =
    if (value < 1) Left(LeafCountError.NonPositive(value))
    else if (value > MaxN) Left(LeafCountError.ExceedsMaximum(value, MaxN))
    else Right(new LeafCount(value) {})
}
