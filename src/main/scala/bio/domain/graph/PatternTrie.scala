package bio.domain.graph

import bio.domain.nucleic.DnaNucleotide

/** Result of the Rosalind TRIE ("Introduction to Pattern Matching") problem — the
  * trie's edges in creation order (see
  * [[bio.algorithms.graph.TrieConstruction.construct]]).
  *
  * `format` renders the adjacency list in the Rosalind TRIE style: each edge as
  * `parent child symbol` on its own line, in order. The empty trie renders as the
  * empty string.
  */
final case class PatternTrie(edges: Vector[TrieEdge]) {

  def format: String =
    edges
      .map(e => s"${e.parent} ${e.child} ${DnaNucleotide.toChar(e.symbol)}")
      .mkString("\n")
}
