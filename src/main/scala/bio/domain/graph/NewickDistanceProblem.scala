package bio.domain.graph

/** Validated input bundle for the Rosalind NWCK ("Distances in Trees") problem —
  * see [[bio.algorithms.graph.NewickDistance.between]].
  *
  * Bundles a parsed [[NewickTree]] with two query labels `x` and `y`. The smart
  * constructor verifies both labels appear in `tree.labels`. Validation order:
  * source label `x` first, then target label `y`; first failure wins.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via
  * [[NewickDistanceProblem.from]].
  */
sealed abstract case class NewickDistanceProblem(
    tree: NewickTree,
    x: String,
    y: String
)

object NewickDistanceProblem {

  def from(
      tree: NewickTree,
      x: String,
      y: String
  ): Either[NewickDistanceProblemError, NewickDistanceProblem] = {
    val labels = tree.labels
    if (!labels.contains(x)) Left(NewickDistanceProblemError.UnknownLabel(x))
    else if (!labels.contains(y)) Left(NewickDistanceProblemError.UnknownLabel(y))
    else Right(new NewickDistanceProblem(tree, x, y) {})
  }
}
