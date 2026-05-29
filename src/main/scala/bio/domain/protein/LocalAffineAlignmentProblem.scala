package bio.domain.protein

/** Validated input bundle for the Rosalind LAFF ("Local Alignment with Affine
  * Gap Penalty") problem — see
  * [[bio.algorithms.protein.LocalAffineAlignment.compute]].
  *
  * Wraps two protein strings — `left` and `right` — whose maximum *local*
  * alignment score and one optimal pair of aligned substrings the algorithm
  * will compute under BLOSUM62 substitution scoring and an *affine* gap
  * penalty `a + b·(L − 1)` with gap-opening penalty `a = 11` and
  * gap-extension penalty `b = 1`.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `left.value.length <= 10000`, else `LeftTooLong`;
  *   2. `right.value.length <= 10000`, else `RightTooLong`.
  *
  * Empty `left` and/or empty `right` are accepted (no positive-scoring local
  * alignment exists, so the result is the zero-score empty alignment).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[LocalAffineAlignmentProblem.from]].
  */
sealed abstract case class LocalAffineAlignmentProblem(
    left: ProteinString,
    right: ProteinString
)

object LocalAffineAlignmentProblem {
  private val MaxLength: Int = 10000

  def from(
      left: ProteinString,
      right: ProteinString
  ): Either[LocalAffineAlignmentProblemError, LocalAffineAlignmentProblem] = {
    val leftLen  = left.value.length
    val rightLen = right.value.length
    if (leftLen > MaxLength)
      Left(LocalAffineAlignmentProblemError.LeftTooLong(leftLen, MaxLength))
    else if (rightLen > MaxLength)
      Left(LocalAffineAlignmentProblemError.RightTooLong(rightLen, MaxLength))
    else
      Right(new LocalAffineAlignmentProblem(left, right) {})
  }
}
