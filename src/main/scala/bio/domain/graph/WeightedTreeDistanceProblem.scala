package bio.domain.graph

/** Validated input for the Rosalind NKEW ("Newick Format with Edge Weights") problem —
  * see [[bio.algorithms.graph.WeightedNewickDistance.between]].
  *
  * Wraps a parsed [[WeightedNewickTree]] and the two query node labels `x` and `y`. The
  * smart constructor requires both labels to occur in the tree (first failure wins):
  * `x`, else `NodeNotFound(x)`; then `y`, else `NodeNotFound(y)`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[WeightedTreeDistanceProblem.from]].
  */
sealed abstract case class WeightedTreeDistanceProblem(
    tree: WeightedNewickTree,
    x: String,
    y: String
)

object WeightedTreeDistanceProblem {

  def from(
      tree: WeightedNewickTree,
      x: String,
      y: String
  ): Either[WeightedTreeDistanceProblemError, WeightedTreeDistanceProblem] = {
    val labels = tree.labels
    if (!labels.contains(x)) Left(WeightedTreeDistanceProblemError.NodeNotFound(x))
    else if (!labels.contains(y)) Left(WeightedTreeDistanceProblemError.NodeNotFound(y))
    else Right(new WeightedTreeDistanceProblem(tree, x, y) {})
  }
}
