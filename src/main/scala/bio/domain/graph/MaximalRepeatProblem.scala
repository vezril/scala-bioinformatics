package bio.domain.graph

import bio.domain.nucleic.DnaString

/** Validated input for the Rosalind MREP ("Identifying Maximal Repeats") problem —
  * see [[bio.algorithms.graph.IdentifyMaximalRepeats.find]].
  *
  * Wraps the DNA string `s` (a [[DnaString]] of length `≤ 1000` bp) and the minimum
  * repeat length to report. The smart constructor enforces, first failure wins:
  *   - `dna.value.length <= 1000`, else `SequenceTooLong`;
  *   - `minLength >= 1`, else `NonPositiveMinLength`.
  *
  * `minLength` is a parameter (Rosalind fixes it at 20) so the algorithm can be
  * exercised on short strings with a smaller threshold.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[MaximalRepeatProblem.from]].
  */
sealed abstract case class MaximalRepeatProblem(dna: DnaString, minLength: Int)

object MaximalRepeatProblem {
  private val MaxLength: Int = 1000

  def from(
      dna: DnaString,
      minLength: Int
  ): Either[MaximalRepeatProblemError, MaximalRepeatProblem] = {
    val length = dna.value.length
    if (length > MaxLength)
      Left(MaximalRepeatProblemError.SequenceTooLong(length, MaxLength))
    else if (minLength < 1)
      Left(MaximalRepeatProblemError.NonPositiveMinLength(minLength))
    else
      Right(new MaximalRepeatProblem(dna, minLength) {})
  }
}
