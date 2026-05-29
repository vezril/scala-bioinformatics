package bio.domain.protein

/** Validated input bundle for the Rosalind LOCA ("Local Alignment with
  * Scoring Matrix") problem — see
  * [[bio.algorithms.protein.LocalAlignment.compute]].
  *
  * Wraps two protein strings — `left` and `right` — for which the algorithm
  * computes the maximum local-alignment score under PAM250 substitution
  * scoring and a linear gap penalty of `-5`, plus the two substrings of the
  * originals that achieve it.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `left.value.length <= 1000`, else `LeftTooLong`;
  *   2. `right.value.length <= 1000`, else `RightTooLong`.
  *
  * Empty `left` and/or empty `right` are accepted (both yield
  * `LocalAlignment(0, "", "")` — no positive-scoring local alignment exists
  * if either input is empty).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[LocalAlignmentProblem.from]].
  */
sealed abstract case class LocalAlignmentProblem(
    left: ProteinString,
    right: ProteinString
)

object LocalAlignmentProblem {
  private val MaxLength: Int = 1000

  def from(
      left: ProteinString,
      right: ProteinString
  ): Either[LocalAlignmentProblemError, LocalAlignmentProblem] = {
    val leftLen  = left.value.length
    val rightLen = right.value.length
    if (leftLen > MaxLength)
      Left(LocalAlignmentProblemError.LeftTooLong(leftLen, MaxLength))
    else if (rightLen > MaxLength)
      Left(LocalAlignmentProblemError.RightTooLong(rightLen, MaxLength))
    else
      Right(new LocalAlignmentProblem(left, right) {})
  }
}
