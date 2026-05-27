package bio.domain.graph

/** Validated input bundle for the Rosalind INOD ("Counting Phylogenetic Ancestors")
  * problem — see [[bio.algorithms.graph.PhylogeneticAncestors.internalNodes]].
  *
  * Wraps the number of leaves `n` of an unrooted binary tree. The Rosalind bounds
  * are `3 <= n <= 10000`:
  *   - the lower bound of 3 is intrinsic — an unrooted binary tree requires at
  *     least three leaves to have any internal node at all (and the formula
  *     `internalNodes = n - 2` only yields a non-negative count for `n >= 2`,
  *     with Rosalind restricting attention to `n >= 3`).
  *   - the upper bound of 10000 is the Rosalind problem constraint.
  *
  * Validation order: lower bound first, then upper bound. First failure wins.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via
  * [[UnrootedBinaryTreeLeafCount.from]].
  */
sealed abstract case class UnrootedBinaryTreeLeafCount(n: Int)

object UnrootedBinaryTreeLeafCount {
  private val MinN: Int = 3
  private val MaxN: Int = 10000

  def from(n: Int): Either[UnrootedBinaryTreeLeafCountError, UnrootedBinaryTreeLeafCount] =
    if (n < MinN) Left(UnrootedBinaryTreeLeafCountError.BelowMinimum(n, MinN))
    else if (n > MaxN) Left(UnrootedBinaryTreeLeafCountError.ExceedsMaximum(n, MaxN))
    else Right(new UnrootedBinaryTreeLeafCount(n) {})
}
