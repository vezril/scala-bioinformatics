package bio.parsing

import bio.domain.graph.UndirectedEdge
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

/** Cats Effect–based reader for the Rosalind TREE-format adjacency list.
  *
  * Input format:
  * {{{
  *   <n>
  *   <u_1> <v_1>
  *   <u_2> <v_2>
  *   ...
  * }}}
  *
  *   - The first non-blank line is the node count `n`.
  *   - Each subsequent non-blank line is one undirected edge `u v` (whitespace
  *     separated). Blank / whitespace-only lines are ignored.
  *
  * Reads the full file contents as UTF-8 on the Cats Effect blocking pool, then runs
  * the pure parser. Failures are surfaced via `Either[TreeAdjacencyError, _]`:
  *   - I/O failures → [[TreeAdjacencyError.IoFailure]]
  *   - Parser failures → [[TreeAdjacencyError.Parse]]
  *
  * Returns the raw `(n, edges)` pair — bundle into a
  * [[bio.domain.graph.TreeCompletionProblem]] via `TreeCompletionProblem.from(n, edges)`
  * if you want the additional `n ≤ 1000` / endpoint-in-range validation.
  *
  * The returned `IO` is referentially transparent — constructing it has no side
  * effect; the file is read only when the `IO` is executed.
  *
  * Sized for the Rosalind use case (`n ≤ 1000`). For arbitrarily large inputs a
  * streaming variant would be preferable — out of scope here.
  */
object TreeAdjacencyFileReader {

  def read(path: Path): IO[Either[TreeAdjacencyError, (Int, Vector[UndirectedEdge])]] =
    IO.blocking(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)).attempt.map {
      case Left(cause)    => Left(TreeAdjacencyError.IoFailure(cause))
      case Right(content) => parse(content).left.map(TreeAdjacencyError.Parse)
    }

  /** Pure parser. Exposed for testing and for callers that already have the content
    * in memory.
    */
  def parse(content: String): Either[TreeAdjacencyParseError, (Int, Vector[UndirectedEdge])] = {
    // Pair each line with its 1-indexed line number BEFORE filtering, so error
    // messages point at the original file position.
    val numberedLines = content.linesIterator.zipWithIndex
      .map { case (line, idx) => (line.trim, idx + 1) }
      .filter { case (line, _) => line.nonEmpty }
      .toVector

    if (numberedLines.isEmpty) Left(TreeAdjacencyParseError.EmptyInput)
    else {
      val (nLine, _) = numberedLines.head
      nLine.toIntOption match {
        case None    => Left(TreeAdjacencyParseError.InvalidN(nLine))
        case Some(n) =>
          numberedLines.tail.foldLeft[Either[TreeAdjacencyParseError, Vector[UndirectedEdge]]](
            Right(Vector.empty)
          ) {
            case (Left(err), _)                  => Left(err)
            case (Right(acc), (line, lineNumber)) => parseEdgeLine(line, lineNumber).map(acc :+ _)
          }.map(edges => (n, edges))
      }
    }
  }

  private def parseEdgeLine(
      line: String,
      lineNumber: Int
  ): Either[TreeAdjacencyParseError, UndirectedEdge] =
    line.split("\\s+").toList match {
      case List(uStr, vStr) =>
        (uStr.toIntOption, vStr.toIntOption) match {
          case (Some(u), Some(v)) =>
            UndirectedEdge.from(u, v).left.map { err =>
              TreeAdjacencyParseError.InvalidEdgeLine(lineNumber, line, err.toString)
            }
          case _ =>
            Left(
              TreeAdjacencyParseError.InvalidEdgeLine(
                lineNumber,
                line,
                "non-integer endpoint"
              )
            )
        }
      case _ =>
        Left(
          TreeAdjacencyParseError.InvalidEdgeLine(
            lineNumber,
            line,
            "expected two whitespace-separated integers"
          )
        )
    }
}
