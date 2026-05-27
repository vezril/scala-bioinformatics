package bio.parsing

/** Pure-parser failures for the TREE-format adjacency list. */
sealed trait TreeAdjacencyParseError
object TreeAdjacencyParseError {

  /** The file was empty / contained only whitespace. */
  case object EmptyInput extends TreeAdjacencyParseError

  /** The first non-blank line could not be parsed as the node count `n`. Carries the
    * offending line verbatim.
    */
  final case class InvalidN(line: String) extends TreeAdjacencyParseError

  /** An edge line could not be parsed. `lineNumber` is 1-indexed against the original
    * file (the `n` line is line 1; the first edge is line 2). `reason` is a short
    * human-readable explanation: "expected two whitespace-separated integers",
    * "non-integer endpoint", or the underlying `UndirectedEdgeError` toString.
    */
  final case class InvalidEdgeLine(lineNumber: Int, line: String, reason: String)
      extends TreeAdjacencyParseError
}
