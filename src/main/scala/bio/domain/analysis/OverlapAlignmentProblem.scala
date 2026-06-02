package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind OAP ("Overlap Alignment") problem —
  * see [[bio.algorithms.analysis.OverlapAlignment.align]].
  *
  * Wraps two DNA strings whose roles are **asymmetric**: an *overlap alignment*
  * aligns a **suffix of `s`** against a **prefix of `t`**.
  *
  *   - `s` is the first string (≤ 10 000 nt) — only a *suffix* participates.
  *   - `t` is the second string (≤ 10 000 nt) — only a *prefix* participates.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `s.value.length <= 10000`, else `STooLong`;
  *   2. `t.value.length <= 10000`, else `TTooLong`.
  *
  * Empty `s` and/or empty `t` are accepted. (`DnaString` already permits the
  * empty string, and its 100 000 cap comfortably covers both OAP bounds.)
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[OverlapAlignmentProblem.from]].
  */
sealed abstract case class OverlapAlignmentProblem(
    s: DnaString,
    t: DnaString
)

object OverlapAlignmentProblem {
  private val MaxLength: Int = 10000

  def from(
      s: DnaString,
      t: DnaString
  ): Either[OverlapAlignmentProblemError, OverlapAlignmentProblem] = {
    val sLen = s.value.length
    val tLen = t.value.length
    if (sLen > MaxLength)
      Left(OverlapAlignmentProblemError.STooLong(sLen, MaxLength))
    else if (tLen > MaxLength)
      Left(OverlapAlignmentProblemError.TTooLong(tLen, MaxLength))
    else
      Right(new OverlapAlignmentProblem(s, t) {})
  }
}
