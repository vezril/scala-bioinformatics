package bio.domain.graph

/** Validated input for the Rosalind LREP ("Finding the Longest Multiple Repeat")
  * problem — see [[bio.algorithms.graph.LongestMultipleRepeat.find]].
  *
  * Wraps the text `s$` (the DNA string `s` with the `$` terminator appended), the
  * positive repeat threshold `k`, and the suffix-tree edge list. The smart constructor
  * enforces, first failure wins:
  *   1. `k >= 1`, else `NonPositiveK`;
  *   2. `text.length <= 20001` (20 kbp + terminator), else `TextTooLong`;
  *   3. each edge's substring `[start, start + length)` lies within the text — `start >= 1`
  *      and `start - 1 + length <= text.length` (index order) — else `EdgeOutOfBounds`.
  *
  * The empty edge list is accepted (a root-only tree yields the empty answer).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[LongestRepeatProblem.from]].
  */
sealed abstract case class LongestRepeatProblem(
    text: String,
    k: Int,
    edges: Vector[SuffixTreeEdge]
)

object LongestRepeatProblem {
  private val MaxTextLength: Int = 20001

  def from(
      text: String,
      k: Int,
      edges: Vector[SuffixTreeEdge]
  ): Either[LongestRepeatProblemError, LongestRepeatProblem] =
    if (k < 1)
      Left(LongestRepeatProblemError.NonPositiveK(k))
    else if (text.length > MaxTextLength)
      Left(LongestRepeatProblemError.TextTooLong(text.length, MaxTextLength))
    else
      firstOutOfBounds(text, edges) match {
        case Some(err) => Left(err)
        case None      => Right(new LongestRepeatProblem(text, k, edges) {})
      }

  /** The first edge (index order) whose substring label falls outside the text. */
  private def firstOutOfBounds(
      text: String,
      edges: Vector[SuffixTreeEdge]
  ): Option[LongestRepeatProblemError] =
    edges.iterator.zipWithIndex.collectFirst {
      case (e, i) if e.start < 1 || e.start - 1 + e.length > text.length =>
        LongestRepeatProblemError.EdgeOutOfBounds(i, e.start, e.length, text.length)
    }
}
