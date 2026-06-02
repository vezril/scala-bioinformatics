package bio.domain.protein

import bio.domain.nucleic.DnaString

/** Validated input for the Rosalind ORF ("Open Reading Frames") problem —
  * see [[bio.algorithms.protein.OpenReadingFrames.find]].
  *
  * Wraps a [[DnaString]] of length `≤ 1000` bp (1 kbp). Characters are already
  * validated by `DnaString`, so the only additional rule is the length cap:
  *   - `dna.value.length <= 1000`, else `SequenceTooLong`.
  *
  * Empty sequences are accepted (they simply yield no candidate proteins).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[OpenReadingFrameProblem.from]].
  */
sealed abstract case class OpenReadingFrameProblem(dna: DnaString)

object OpenReadingFrameProblem {
  private val MaxLength: Int = 1000

  def from(
      dna: DnaString
  ): Either[OpenReadingFrameProblemError, OpenReadingFrameProblem] = {
    val length = dna.value.length
    if (length > MaxLength)
      Left(OpenReadingFrameProblemError.SequenceTooLong(length, MaxLength))
    else
      Right(new OpenReadingFrameProblem(dna) {})
  }
}
