package bio.domain.graph

/** Result of the Rosalind LREP ("Finding the Longest Multiple Repeat") problem — the
  * longest substring of `s` occurring at least `k` times (see
  * [[bio.algorithms.graph.LongestMultipleRepeat.find]]).
  *
  * `format` returns the substring verbatim; the empty substring (no qualifying repeat)
  * renders as the empty string.
  */
final case class LongestRepeat(substring: String) {

  def format: String = substring
}
