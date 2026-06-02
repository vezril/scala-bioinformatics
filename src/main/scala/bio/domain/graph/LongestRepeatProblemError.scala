package bio.domain.graph

/** Validation errors for [[LongestRepeatProblem]] (Rosalind LREP). */
sealed trait LongestRepeatProblemError

object LongestRepeatProblemError {

  /** The repeat threshold `k` is not a positive integer. */
  final case class NonPositiveK(k: Int) extends LongestRepeatProblemError

  /** The text `s$` exceeds the maximum allowed length. */
  final case class TextTooLong(length: Int, max: Int) extends LongestRepeatProblemError

  /** The edge at `index` references a substring `[start, start + length)` that does not
    * lie within the text of length `textLength`.
    */
  final case class EdgeOutOfBounds(index: Int, start: Int, length: Int, textLength: Int)
      extends LongestRepeatProblemError
}
