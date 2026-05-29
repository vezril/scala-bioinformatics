package bio.domain.protein

/** Validated input bundle for the Rosalind GAFF ("Global Alignment with
  * Scoring Matrix and Affine Gap Penalty") problem — see
  * [[bio.algorithms.protein.AffineGapAlignment.compute]].
  *
  * Wraps two protein strings — `left` and `right` — whose maximum global
  * alignment score and one optimal alignment the algorithm will compute
  * under BLOSUM62 substitution scoring and an *affine* gap penalty
  * `a + b·(L − 1)` with gap-opening penalty `a = 11` and gap-extension
  * penalty `b = 1`.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `left.value.length <= 100`, else `LeftTooLong`;
  *   2. `right.value.length <= 100`, else `RightTooLong`.
  *
  * Empty `left` and/or empty `right` are accepted (a single non-empty side
  * is one affine gap spanning that side).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[AffineGapAlignmentProblem.from]].
  */
sealed abstract case class AffineGapAlignmentProblem(
    left: ProteinString,
    right: ProteinString
)

object AffineGapAlignmentProblem {
  private val MaxLength: Int = 100

  def from(
      left: ProteinString,
      right: ProteinString
  ): Either[AffineGapAlignmentProblemError, AffineGapAlignmentProblem] = {
    val leftLen  = left.value.length
    val rightLen = right.value.length
    if (leftLen > MaxLength)
      Left(AffineGapAlignmentProblemError.LeftTooLong(leftLen, MaxLength))
    else if (rightLen > MaxLength)
      Left(AffineGapAlignmentProblemError.RightTooLong(rightLen, MaxLength))
    else
      Right(new AffineGapAlignmentProblem(left, right) {})
  }
}
