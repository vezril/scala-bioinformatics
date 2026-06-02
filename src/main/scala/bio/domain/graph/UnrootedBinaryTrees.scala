package bio.domain.graph

/** Result of the Rosalind EUBT ("Enumerating Unrooted Binary Trees") problem — the
  * Newick strings of every unrooted binary tree on the given taxa (see
  * [[bio.algorithms.graph.EnumerateUnrootedBinaryTrees.enumerate]]).
  *
  * `format` renders one tree per line (any order). The empty result renders as the
  * empty string.
  */
final case class UnrootedBinaryTrees(trees: Vector[String]) {

  def format: String = trees.mkString("\n")
}
