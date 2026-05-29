package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind LCSQ ("Finding a Shared Spliced
  * Motif") problem — see [[bio.algorithms.analysis.SharedSplicedMotif.find]].
  *
  * Wraps two DNA strings — `left` and `right` — whose *longest common
  * subsequence* the algorithm will find. The LCS problem is symmetric in its
  * two inputs, so the names `left`/`right` carry no source-vs-target
  * connotation (contrast with [[SplicedMotifProblem]] where `source` and
  * `target` are asymmetric).
  *
  * The smart constructor enforces:
  *   1. `left.value.length <= 1000`, else `LeftTooLong`;
  *   2. `right.value.length <= 1000`, else `RightTooLong`.
  *
  * Empty `left` and/or empty `right` are accepted (the LCS of any string with
  * the empty string is `""`).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[SharedSplicedMotifProblem.from]].
  */
sealed abstract case class SharedSplicedMotifProblem(
    left: DnaString,
    right: DnaString
)

object SharedSplicedMotifProblem {
  private val MaxLength: Int = 1000

  def from(
      left: DnaString,
      right: DnaString
  ): Either[SharedSplicedMotifProblemError, SharedSplicedMotifProblem] = {
    val leftLen  = left.value.length
    val rightLen = right.value.length
    if (leftLen > MaxLength)
      Left(SharedSplicedMotifProblemError.LeftTooLong(leftLen, MaxLength))
    else if (rightLen > MaxLength)
      Left(SharedSplicedMotifProblemError.RightTooLong(rightLen, MaxLength))
    else
      Right(new SharedSplicedMotifProblem(left, right) {})
  }
}
