package bio.domain.graph

/** Validated input bundle for the Rosalind CNTQ ("Counting Quartets") problem —
  * see [[bio.algorithms.graph.CountingQuartets.count]].
  *
  * Bundles a declared leaf count `n` with a parsed unrooted binary [[NewickTree]]
  * `T` on `n` taxa. The smart constructor validates, first-failure-wins, in the
  * order:
  *   1. `n` at least `4` (the smallest tree admitting a quartet);
  *   2. `n` at most `5000` (the Rosalind bound; also keeps the count arithmetic
  *      within `Long` range);
  *   3. the tree's leaf count equal to `n`.
  *
  * The tree is taken already parsed (parse failures are handled at the edge by the
  * caller), mirroring [[SplitDistanceProblem]]. Implemented as
  * `sealed abstract case class` so the synthesized `apply`/`copy` cannot leak
  * around the smart constructor — construct via [[CountingQuartetsProblem.from]].
  */
sealed abstract case class CountingQuartetsProblem(n: Int, tree: NewickTree)

object CountingQuartetsProblem {
  private val MinN: Int = 4
  private val MaxN: Int = 5000

  def from(
      n: Int,
      tree: NewickTree
  ): Either[CountingQuartetsProblemError, CountingQuartetsProblem] =
    if (n < MinN) Left(CountingQuartetsProblemError.BelowMinimum(n, MinN))
    else if (n > MaxN) Left(CountingQuartetsProblemError.ExceedsMaximum(n, MaxN))
    else {
      val leaves = leafCount(tree)
      if (leaves != n) Left(CountingQuartetsProblemError.LeafCountMismatch(n, leaves))
      else Right(new CountingQuartetsProblem(n, tree) {})
    }

  /** The number of leaf nodes (nodes with no children) in `tree`. */
  private def leafCount(tree: NewickTree): Int =
    if (tree.children.isEmpty) 1
    else tree.children.foldLeft(0)(_ + leafCount(_))
}
