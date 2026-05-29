package bio.domain.protein

/** Validated input bundle for the Rosalind EDTA ("Edit Distance Alignment")
  * problem — see [[bio.algorithms.protein.EditDistanceAlignment.align]].
  *
  * Wraps two protein strings — `left` and `right` — for which the algorithm
  * computes the Levenshtein distance *and* one optimal alignment (two
  * augmented strings with `-` gap symbols inserted).
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `left.value.length <= 1000`, else `LeftTooLong`;
  *   2. `right.value.length <= 1000`, else `RightTooLong`.
  *
  * Empty `left` and/or empty `right` are accepted.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[EditDistanceAlignmentProblem.from]].
  */
sealed abstract case class EditDistanceAlignmentProblem(
    left: ProteinString,
    right: ProteinString
)

object EditDistanceAlignmentProblem {
  private val MaxLength: Int = 1000

  def from(
      left: ProteinString,
      right: ProteinString
  ): Either[EditDistanceAlignmentProblemError, EditDistanceAlignmentProblem] = {
    val leftLen  = left.value.length
    val rightLen = right.value.length
    if (leftLen > MaxLength)
      Left(EditDistanceAlignmentProblemError.LeftTooLong(leftLen, MaxLength))
    else if (rightLen > MaxLength)
      Left(EditDistanceAlignmentProblemError.RightTooLong(rightLen, MaxLength))
    else
      Right(new EditDistanceAlignmentProblem(left, right) {})
  }
}
