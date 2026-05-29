package bio.domain.protein

/** Validated input bundle for the Rosalind EDIT ("Edit Distance") problem —
  * see [[bio.algorithms.protein.EditDistance.compute]].
  *
  * Wraps two protein strings — `left` and `right` — whose Levenshtein
  * (edit) distance the algorithm will compute. The edit-distance problem is
  * symmetric in its two inputs, so the names `left`/`right` carry no
  * source-vs-target connotation.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `left.value.length <= 1000`, else `LeftTooLong`;
  *   2. `right.value.length <= 1000`, else `RightTooLong`.
  *
  * Empty `left` and/or empty `right` are accepted (`d(∅, t) = |t|`,
  * `d(s, ∅) = |s|`).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[EditDistanceProblem.from]].
  */
sealed abstract case class EditDistanceProblem(
    left: ProteinString,
    right: ProteinString
)

object EditDistanceProblem {
  private val MaxLength: Int = 1000

  def from(
      left: ProteinString,
      right: ProteinString
  ): Either[EditDistanceProblemError, EditDistanceProblem] = {
    val leftLen  = left.value.length
    val rightLen = right.value.length
    if (leftLen > MaxLength)
      Left(EditDistanceProblemError.LeftTooLong(leftLen, MaxLength))
    else if (rightLen > MaxLength)
      Left(EditDistanceProblemError.RightTooLong(rightLen, MaxLength))
    else
      Right(new EditDistanceProblem(left, right) {})
  }
}
