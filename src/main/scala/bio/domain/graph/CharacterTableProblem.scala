package bio.domain.graph

/** Validated input bundle for the Rosalind CSTR ("Creating a Character Table")
  * problem — see [[bio.algorithms.graph.CharacterTable.compute]].
  *
  * Bundles a parsed [[NewickTree]] with its pre-computed, lexicographically-sorted
  * leaf-label vector. The smart constructor enforces the Rosalind cap of `≤ 200`
  * labelled leaves (taxa). Computing `leafLabels` once at construction time means
  * the algorithm, the column-order code, and the trivial-split filter all agree
  * on what counts as a leaf — and avoids re-walking the tree downstream.
  *
  * **Leaf identification** — a node is a labelled leaf iff `children.isEmpty &&
  * label.isDefined`. Unlabelled leaves (rare but representable in Newick) are
  * silently skipped from the taxa set; the Rosalind input never has them.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[CharacterTableProblem.from]].
  */
sealed abstract case class CharacterTableProblem(
    tree: NewickTree,
    leafLabels: Vector[String]
)

object CharacterTableProblem {
  private val MaxTaxa: Int = 200

  def from(
      tree: NewickTree
  ): Either[CharacterTableProblemError, CharacterTableProblem] = {
    val labels = collectLeaves(tree).distinct.sorted
    if (labels.size > MaxTaxa)
      Left(CharacterTableProblemError.TooManyTaxa(labels.size, MaxTaxa))
    else
      Right(new CharacterTableProblem(tree, labels) {})
  }

  /** Recursively collects every labelled-leaf identifier in the tree. A node is a
    * labelled leaf iff it has no children and its `label` is `Some(_)`.
    */
  private def collectLeaves(node: NewickTree): Vector[String] =
    if (node.children.isEmpty) node.label.toVector
    else node.children.flatMap(collectLeaves)
}
