package bio.domain.nucleic

/** Validated input for the Rosalind TRAN ("Transitions and Transversions")
  * problem — see [[bio.algorithms.nucleic.TransitionTransversionAnalysis.analyze]].
  *
  * Wraps a `first` and a `second` [[DnaString]]. The smart constructor enforces,
  * with first-failure-wins ordering: each sequence length ≤ 1000 bp, then equal
  * length (the ratio is only defined for equal-length strings). Character
  * validity (`A`,`C`,`G`,`T`) is enforced upstream by [[DnaString]]. Two equal
  * empty sequences are accepted (ratio `0.0`).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[TransitionTransversionProblem.from]].
  */
sealed abstract case class TransitionTransversionProblem(first: DnaString, second: DnaString)

object TransitionTransversionProblem {
  private val MaxLength: Int = 1000

  def from(
      first: DnaString,
      second: DnaString
  ): Either[TransitionTransversionProblemError, TransitionTransversionProblem] = {
    val firstLen  = first.value.length
    val secondLen = second.value.length
    if (firstLen > MaxLength)
      Left(TransitionTransversionProblemError.SequenceTooLong(firstLen, MaxLength))
    else if (secondLen > MaxLength)
      Left(TransitionTransversionProblemError.SequenceTooLong(secondLen, MaxLength))
    else if (firstLen != secondLen)
      Left(TransitionTransversionProblemError.LengthMismatch(firstLen, secondLen))
    else
      Right(new TransitionTransversionProblem(first, second) {})
  }
}
