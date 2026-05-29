package bio.domain.protein

/** Validated input bundle for the Rosalind CTEA ("Counting Optimal
  * Alignments") problem — see
  * [[bio.algorithms.protein.OptimalAlignmentCount.compute]].
  *
  * Wraps two protein strings — `left` and `right` — for which the algorithm
  * counts the number of distinct optimal alignments under the standard
  * Levenshtein metric, returning the value modulo `134_217_727 = 2^27 - 1`.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `left.value.length <= 1000`, else `LeftTooLong`;
  *   2. `right.value.length <= 1000`, else `RightTooLong`.
  *
  * Empty `left` and/or empty `right` are accepted (each empty/empty,
  * empty/non-empty, and non-empty/empty case has exactly one optimal
  * alignment).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[OptimalAlignmentCountProblem.from]].
  */
sealed abstract case class OptimalAlignmentCountProblem(
    left: ProteinString,
    right: ProteinString
)

object OptimalAlignmentCountProblem {
  private val MaxLength: Int = 1000

  def from(
      left: ProteinString,
      right: ProteinString
  ): Either[OptimalAlignmentCountProblemError, OptimalAlignmentCountProblem] = {
    val leftLen  = left.value.length
    val rightLen = right.value.length
    if (leftLen > MaxLength)
      Left(OptimalAlignmentCountProblemError.LeftTooLong(leftLen, MaxLength))
    else if (rightLen > MaxLength)
      Left(OptimalAlignmentCountProblemError.RightTooLong(rightLen, MaxLength))
    else
      Right(new OptimalAlignmentCountProblem(left, right) {})
  }
}
