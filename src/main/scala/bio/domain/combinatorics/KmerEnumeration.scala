package bio.domain.combinatorics

/** Result of enumerating all length-`n` strings over an ordered alphabet
  * (Rosalind LEXF) — produced by
  * [[bio.algorithms.combinatorics.EnumerateKmers.enumerate]].
  *
  * `kmers` holds the strings in lexicographic order; [[format]] renders them one
  * per line for output.
  */
final case class KmerEnumeration(kmers: Vector[String]) {
  def format: String = kmers.mkString("\n")
}
