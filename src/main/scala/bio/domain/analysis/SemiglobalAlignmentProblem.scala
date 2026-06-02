package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind SMGB ("Semiglobal Alignment")
  * problem — see [[bio.algorithms.analysis.SemiglobalAlignment.align]].
  *
  * Wraps two DNA strings aligned under the **symmetric** free-end-gap regime: a
  * *semiglobal alignment* aligns **all of `s`** against **all of `t`**, but gap
  * runs at the **leading or trailing ends of either string are free** (do not
  * contribute to the score).
  *
  *   - `s` is the first string (≤ 10 000 nt).
  *   - `t` is the second string (≤ 10 000 nt).
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `s.value.length <= 10000`, else `STooLong`;
  *   2. `t.value.length <= 10000`, else `TTooLong`.
  *
  * Empty `s` and/or empty `t` are accepted. (`DnaString` already permits the
  * empty string, and its 100 000 cap comfortably covers both SMGB bounds.)
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[SemiglobalAlignmentProblem.from]].
  */
sealed abstract case class SemiglobalAlignmentProblem(
    s: DnaString,
    t: DnaString
)

object SemiglobalAlignmentProblem {
  private val MaxLength: Int = 10000

  def from(
      s: DnaString,
      t: DnaString
  ): Either[SemiglobalAlignmentProblemError, SemiglobalAlignmentProblem] = {
    val sLen = s.value.length
    val tLen = t.value.length
    if (sLen > MaxLength)
      Left(SemiglobalAlignmentProblemError.STooLong(sLen, MaxLength))
    else if (tLen > MaxLength)
      Left(SemiglobalAlignmentProblemError.TTooLong(tLen, MaxLength))
    else
      Right(new SemiglobalAlignmentProblem(s, t) {})
  }
}
