package bio.domain.protein

/** Validated input bundle for the Rosalind GCON ("Global Alignment with
  * Constant Gap Penalty") problem — see
  * [[bio.algorithms.protein.ConstantGapAlignmentScore.compute]].
  *
  * Wraps two protein strings — `left` and `right` — whose maximum global
  * alignment score the algorithm will compute under BLOSUM62 substitution
  * scoring and a *constant* gap penalty of `5` (every maximal run of
  * contiguous insertions or deletions costs a flat `5`, independent of its
  * length).
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `left.value.length <= 1000`, else `LeftTooLong`;
  *   2. `right.value.length <= 1000`, else `RightTooLong`.
  *
  * Empty `left` and/or empty `right` are accepted (a single non-empty side
  * is one constant gap, scoring `-5`).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[ConstantGapAlignmentScoreProblem.from]].
  */
sealed abstract case class ConstantGapAlignmentScoreProblem(
    left: ProteinString,
    right: ProteinString
)

object ConstantGapAlignmentScoreProblem {
  private val MaxLength: Int = 1000

  def from(
      left: ProteinString,
      right: ProteinString
  ): Either[ConstantGapAlignmentScoreProblemError, ConstantGapAlignmentScoreProblem] = {
    val leftLen  = left.value.length
    val rightLen = right.value.length
    if (leftLen > MaxLength)
      Left(ConstantGapAlignmentScoreProblemError.LeftTooLong(leftLen, MaxLength))
    else if (rightLen > MaxLength)
      Left(ConstantGapAlignmentScoreProblemError.RightTooLong(rightLen, MaxLength))
    else
      Right(new ConstantGapAlignmentScoreProblem(left, right) {})
  }
}
