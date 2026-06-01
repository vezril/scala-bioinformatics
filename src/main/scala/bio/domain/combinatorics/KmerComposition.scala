package bio.domain.combinatorics

/** Result of the Rosalind KMER ("k-Mer Composition") problem — produced by
  * [[bio.algorithms.combinatorics.ComposeKmers.compose]].
  *
  * `counts` holds one occurrence count per possible length-`k` DNA word, in the
  * lexicographic order of those words (`A < C < G < T`). [[format]] renders the
  * counts space-separated for output.
  */
final case class KmerComposition(counts: Vector[Int]) {
  def format: String = counts.mkString(" ")
}
