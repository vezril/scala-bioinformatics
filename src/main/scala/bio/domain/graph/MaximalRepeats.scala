package bio.domain.graph

/** Result of the Rosalind MREP ("Identifying Maximal Repeats") problem — the maximal
  * repeats of the DNA string with length at least the requested minimum (see
  * [[bio.algorithms.graph.IdentifyMaximalRepeats.find]]).
  *
  * `format` renders one repeat per line (any order). The empty result renders as the
  * empty string.
  */
final case class MaximalRepeats(repeats: Vector[String]) {

  def format: String = repeats.mkString("\n")
}
