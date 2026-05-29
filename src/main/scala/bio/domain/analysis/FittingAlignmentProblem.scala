package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind SIMS ("Finding a Motif with
  * Modifications") problem — see
  * [[bio.algorithms.analysis.FittingAlignment.align]].
  *
  * Wraps two DNA strings whose roles are **asymmetric** (contrast with the
  * symmetric `left`/`right` of [[SharedSplicedMotifProblem]]):
  *
  *   - `text` is the long string `s` (≤ 10 000 nt) — only a *substring* of
  *     it participates in the optimal fitting alignment;
  *   - `motif` is the short pattern `t` (≤ 1 000 nt) — *all* of it is
  *     aligned against the chosen substring of `text`.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `text.value.length <= 10000`, else `TextTooLong`;
  *   2. `motif.value.length <= 1000`, else `MotifTooLong`.
  *
  * Empty `text` and/or empty `motif` are accepted. (`DnaString` already
  * permits the empty string, and its 100 000 cap comfortably covers both
  * SIMS bounds.)
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[FittingAlignmentProblem.from]].
  */
sealed abstract case class FittingAlignmentProblem(
    text: DnaString,
    motif: DnaString
)

object FittingAlignmentProblem {
  private val MaxTextLength: Int  = 10000
  private val MaxMotifLength: Int = 1000

  def from(
      text: DnaString,
      motif: DnaString
  ): Either[FittingAlignmentProblemError, FittingAlignmentProblem] = {
    val textLen  = text.value.length
    val motifLen = motif.value.length
    if (textLen > MaxTextLength)
      Left(FittingAlignmentProblemError.TextTooLong(textLen, MaxTextLength))
    else if (motifLen > MaxMotifLength)
      Left(FittingAlignmentProblemError.MotifTooLong(motifLen, MaxMotifLength))
    else
      Right(new FittingAlignmentProblem(text, motif) {})
  }
}
