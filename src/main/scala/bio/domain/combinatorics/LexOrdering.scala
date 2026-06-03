package bio.domain.combinatorics

/** Result of the Rosalind LEXV ("Ordering Strings of Varying Length
  * Lexicographically") problem — produced by
  * [[bio.algorithms.combinatorics.VaryingLengthLexOrder.enumerate]].
  *
  * `strings` holds every string of length 1 to `n` over the alphabet in
  * varying-length lexicographic order; `format` renders them one per line.
  */
final case class LexOrdering(strings: Vector[String]) {
  def format: String = strings.mkString("\n")
}
