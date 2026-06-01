package bio.domain.graph

/** Validated input bundle for the Rosalind QRTD ("Quartet Distance") problem —
  * see [[bio.algorithms.graph.QuartetDistance.compute]].
  *
  * Bundles a taxa list with two unrooted binary [[NewickTree]]s (`T1`, `T2`) on
  * those taxa. The smart constructor validates, first-failure-wins, in the order:
  *   1. taxa non-empty;
  *   2. taxa distinct;
  *   3. `tree1`'s leaf labels equal the taxa set;
  *   4. `tree2`'s leaf labels equal the taxa set.
  *
  * Trees are taken already parsed (parse failures are handled at the edge by the
  * caller), mirroring [[SplitDistanceProblem]]. Implemented as
  * `sealed abstract case class` so the synthesized `apply`/`copy` cannot leak
  * around the smart constructor — construct via [[QuartetDistanceProblem.from]].
  */
sealed abstract case class QuartetDistanceProblem(
    taxa: Vector[String],
    tree1: NewickTree,
    tree2: NewickTree
)

object QuartetDistanceProblem {

  def from(
      taxa: Vector[String],
      tree1: NewickTree,
      tree2: NewickTree
  ): Either[QuartetDistanceProblemError, QuartetDistanceProblem] =
    if (taxa.isEmpty) Left(QuartetDistanceProblemError.EmptyTaxa)
    else
      firstDuplicate(taxa) match {
        case Some(name) => Left(QuartetDistanceProblemError.DuplicateTaxon(name))
        case None =>
          val taxaSet = taxa.toSet
          mismatch(1, taxaSet, tree1)
            .orElse(mismatch(2, taxaSet, tree2))
            .toLeft(new QuartetDistanceProblem(taxa, tree1, tree2) {})
      }

  /** The first taxon name (in order) that repeats an earlier one, if any. */
  private def firstDuplicate(taxa: Vector[String]): Option[String] =
    taxa
      .foldLeft((Set.empty[String], Option.empty[String])) {
        case ((seen, found), name) =>
          if (found.isDefined) (seen, found)
          else if (seen.contains(name)) (seen, Some(name))
          else (seen + name, None)
      }
      ._2

  /** A [[QuartetDistanceProblemError.TreeTaxaMismatch]] if `tree`'s leaf labels
    * differ from `taxaSet`, otherwise `None`.
    */
  private def mismatch(
      treeIndex: Int,
      taxaSet: Set[String],
      tree: NewickTree
  ): Option[QuartetDistanceProblemError] = {
    val leaves  = leafLabels(tree)
    val missing = taxaSet.diff(leaves)
    val extra   = leaves.diff(taxaSet)
    if (missing.isEmpty && extra.isEmpty) None
    else Some(QuartetDistanceProblemError.TreeTaxaMismatch(treeIndex, missing, extra))
  }

  /** The set of labels on leaf nodes (nodes with no children) of `tree`. */
  private def leafLabels(tree: NewickTree): Set[String] =
    if (tree.children.isEmpty) tree.label.toSet
    else tree.children.foldLeft(Set.empty[String])(_ ++ leafLabels(_))
}
