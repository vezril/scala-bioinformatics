package bio.domain.graph

/** A parsed Newick-format tree (see Rosalind NWCK).
  *
  * A single recursive case class models every node type in the grammar:
  *   - **leaf** — `children == Vector.empty` and `label == Some(_)` (e.g. `cat` in `(cat)dog;`)
  *   - **labelled internal node** — non-empty children and `label == Some(_)` (e.g. `dog`)
  *   - **unlabelled internal node** — non-empty children and `label == None`
  *     (e.g. the root of `(dog,cat);`)
  *
  * Children order is preserved as parsed so the structure round-trips cleanly even
  * though [[bio.algorithms.graph.NewickDistance]] treats the tree as undirected.
  *
  * @param label    optional textual identifier (Newick allows unlabelled internal nodes)
  * @param children ordered subtrees beneath this node; empty for a leaf
  */
final case class NewickTree(label: Option[String], children: Vector[NewickTree]) {

  /** The set of all non-empty labels appearing anywhere in this subtree. Used by
    * [[NewickDistanceProblem.from]] to validate that query endpoints actually
    * exist in the tree.
    */
  def labels: Set[String] =
    children.foldLeft(label.toSet)(_ ++ _.labels)

  /** Renders this tree as a canonical Newick string terminated by a single `;`.
    * A leaf renders as its bare label; an internal node renders as its
    * comma-joined children wrapped in parentheses, followed by its label if any.
    * (The trees produced by [[bio.algorithms.graph.CharacterBasedPhylogeny.build]]
    * have unlabelled internal nodes, so internal labels are omitted there.)
    */
  def render: String = renderNode + ";"

  private def renderNode: String =
    if (children.isEmpty) label.getOrElse("")
    else children.map(_.renderNode).mkString("(", ",", ")") + label.getOrElse("")
}
