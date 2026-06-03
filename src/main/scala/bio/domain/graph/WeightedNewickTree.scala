package bio.domain.graph

/** A parsed weighted Newick tree (Rosalind NKEW) — a node with an optional `label` and
  * child subtrees each carrying the weight of the edge connecting it to this node.
  *
  * Unlike the unweighted [[NewickTree]], every edge here has a numeric weight, so the
  * distance between two nodes is the sum of weights along their connecting path.
  */
final case class WeightedNewickTree(label: Option[String], children: Vector[WeightedChild]) {

  /** Every non-empty label appearing anywhere in this subtree. */
  def labels: Set[String] =
    children.foldLeft(label.toSet)(_ ++ _.subtree.labels)
}

/** A child subtree together with the weight of the edge to it. */
final case class WeightedChild(subtree: WeightedNewickTree, weight: Double)
