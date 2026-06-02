package bio.domain.graph

/** One edge of a suffix tree (Rosalind LREP): an edge from `parent` to `child` whose
  * substring label is `text.substring(start - 1, start - 1 + length)` — `start` is the
  * 1-based position of the label in the text `s$`.
  *
  * Public constructor — a `SuffixTreeEdge` carries no invariant beyond holding two node
  * labels and two integers (bounds are validated by [[LongestRepeatProblem]]), so no
  * smart-constructor ceremony is needed (mirrors [[TrieEdge]]).
  */
final case class SuffixTreeEdge(parent: String, child: String, start: Int, length: Int)
