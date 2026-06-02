package bio.domain.nucleic

/** Validated input for the Rosalind REVP ("Locating Restriction Sites") problem —
  * see [[bio.algorithms.nucleic.RestrictionSites.locate]].
  *
  * Wraps a [[DnaString]] of length `≤ 1000` bp (1 kbp). Characters are already
  * validated by `DnaString`, so the only additional rule is the length cap:
  *   - `dna.value.length <= 1000`, else `SequenceTooLong`.
  *
  * Empty sequences are accepted (they simply contain no restriction sites).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[RestrictionSiteProblem.from]].
  */
sealed abstract case class RestrictionSiteProblem(dna: DnaString)

object RestrictionSiteProblem {
  private val MaxLength: Int = 1000

  def from(
      dna: DnaString
  ): Either[RestrictionSiteProblemError, RestrictionSiteProblem] = {
    val length = dna.value.length
    if (length > MaxLength)
      Left(RestrictionSiteProblemError.SequenceTooLong(length, MaxLength))
    else
      Right(new RestrictionSiteProblem(dna) {})
  }
}
