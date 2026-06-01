package bio.domain.graph

/** The result of constructing a de Bruijn graph `B_k` — the de-duplicated,
  * lexicographically sorted directed edges over `S ∪ S^rc` (see
  * [[bio.algorithms.graph.DeBruijnGraphConstruction.construct]]).
  *
  * `format` renders the adjacency list in the Rosalind DBRU style: each edge as
  * `(from, to)` on its own line, in order.
  */
final case class DeBruijnGraph(edges: Vector[DeBruijnEdge]) {
  def format: String = edges.map(e => s"(${e.from}, ${e.to})").mkString("\n")
}
