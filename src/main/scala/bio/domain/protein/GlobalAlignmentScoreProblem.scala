package bio.domain.protein

/** Validated input bundle for the Rosalind GLOB ("Global Alignment with
  * Scoring Matrix") problem — see
  * [[bio.algorithms.protein.GlobalAlignmentScore.compute]].
  *
  * Wraps two protein strings — `left` and `right` — whose maximum global
  * alignment score the algorithm will compute under BLOSUM62 substitution
  * scoring and a linear gap penalty of `-5`.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `left.value.length <= 1000`, else `LeftTooLong`;
  *   2. `right.value.length <= 1000`, else `RightTooLong`.
  *
  * Empty `left` and/or empty `right` are accepted (`d(∅, t) = -5|t|`,
  * `d(s, ∅) = -5|s|`).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[GlobalAlignmentScoreProblem.from]].
  */
sealed abstract case class GlobalAlignmentScoreProblem(
    left: ProteinString,
    right: ProteinString
)

object GlobalAlignmentScoreProblem {
  private val MaxLength: Int = 1000

  def from(
      left: ProteinString,
      right: ProteinString
  ): Either[GlobalAlignmentScoreProblemError, GlobalAlignmentScoreProblem] = {
    val leftLen  = left.value.length
    val rightLen = right.value.length
    if (leftLen > MaxLength)
      Left(GlobalAlignmentScoreProblemError.LeftTooLong(leftLen, MaxLength))
    else if (rightLen > MaxLength)
      Left(GlobalAlignmentScoreProblemError.RightTooLong(rightLen, MaxLength))
    else
      Right(new GlobalAlignmentScoreProblem(left, right) {})
  }
}
