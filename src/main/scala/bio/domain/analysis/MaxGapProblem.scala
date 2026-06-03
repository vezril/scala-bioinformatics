package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input for the Rosalind MGAP ("Maximizing the Gap Symbols of an Optimal
  * Alignment") problem — see [[bio.algorithms.analysis.MaximizeGapSymbols.maxGaps]].
  *
  * Wraps the two DNA strings `s` and `t`, each of length `≤ 5000` bp. The smart
  * constructor enforces the per-sequence length cap with first-failure-wins ordering
  * (`s` then `t`); empty sequences are accepted.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[MaxGapProblem.from]].
  */
sealed abstract case class MaxGapProblem(s: DnaString, t: DnaString)

object MaxGapProblem {
  private val MaxLength: Int = 5000

  def from(s: DnaString, t: DnaString): Either[MaxGapProblemError, MaxGapProblem] =
    if (s.value.length > MaxLength)
      Left(MaxGapProblemError.SequenceTooLong(s.value.length, MaxLength))
    else if (t.value.length > MaxLength)
      Left(MaxGapProblemError.SequenceTooLong(t.value.length, MaxLength))
    else
      Right(new MaxGapProblem(s, t) {})
}
