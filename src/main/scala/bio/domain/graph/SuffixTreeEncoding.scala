package bio.domain.graph

/** Result of the Rosalind SUFF ("Encoding Suffix Trees") problem — the substrings of
  * `s$` labelling the edges of the suffix tree (see
  * [[bio.algorithms.graph.SuffixTreeConstruction.encode]]).
  *
  * `format` renders one edge label per line (any order). The empty result renders as
  * the empty string.
  */
final case class SuffixTreeEncoding(edges: Vector[String]) {

  def format: String = edges.mkString("\n")
}
