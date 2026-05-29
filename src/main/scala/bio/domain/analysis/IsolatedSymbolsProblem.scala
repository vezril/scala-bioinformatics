package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind OSYM ("Isolating Symbols in
  * Alignments") problem — see
  * [[bio.algorithms.analysis.IsolatedSymbols.compute]].
  *
  * Wraps two DNA strings whose roles are **symmetric**: swapping `left`
  * (`s`) and `right` (`t`) transposes the symbol-isolation matrix `M`
  * (preserving its sum) and leaves the global alignment score unchanged.
  * The names therefore carry no source-vs-target connotation (contrast
  * [[FittingAlignmentProblem]]'s asymmetric `text`/`motif`).
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `left.value.length <= 1000`, else `LeftTooLong`;
  *   2. `right.value.length <= 1000`, else `RightTooLong`.
  *
  * Empty `left` and/or empty `right` are accepted.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[IsolatedSymbolsProblem.from]].
  */
sealed abstract case class IsolatedSymbolsProblem(
    left: DnaString,
    right: DnaString
)

object IsolatedSymbolsProblem {
  private val MaxLength: Int = 1000

  def from(
      left: DnaString,
      right: DnaString
  ): Either[IsolatedSymbolsProblemError, IsolatedSymbolsProblem] = {
    val leftLen  = left.value.length
    val rightLen = right.value.length
    if (leftLen > MaxLength)
      Left(IsolatedSymbolsProblemError.LeftTooLong(leftLen, MaxLength))
    else if (rightLen > MaxLength)
      Left(IsolatedSymbolsProblemError.RightTooLong(rightLen, MaxLength))
    else
      Right(new IsolatedSymbolsProblem(left, right) {})
  }
}
