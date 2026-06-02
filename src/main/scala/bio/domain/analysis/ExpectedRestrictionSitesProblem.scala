package bio.domain.analysis

import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability

/** Parameters for the Rosalind EVAL ("Expected Number of Restriction Sites")
  * problem — see [[bio.algorithms.analysis.ExpectedRestrictionSites.expectedCounts]].
  *
  * Holds a motif, a random-string length `n`, and an array of GC-content fractions.
  * Each GC-content is a `Probability` (a fraction in `[0,1]`).
  *
  * Constructable only via [[ExpectedRestrictionSitesProblem.from]], which enforces
  * the Rosalind bounds with first-failure-wins ordering:
  *   - `motif.value.length <= 10`, else `MotifTooLong`
  *   - `motif.value.length` is even, else `OddMotifLength`
  *   - `length >= 1`, else `NonPositiveLength`
  *   - `length <= 1000000`, else `LengthTooLarge`
  *   - `gcContents.size <= 20`, else `TooManyGcContents`
  *
  * An empty motif (length 0, even) and an empty GC-content array are accepted
  * boundaries.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class ExpectedRestrictionSitesProblem(
    motif: DnaString,
    length: Int,
    gcContents: Vector[Probability]
)

object ExpectedRestrictionSitesProblem {
  private val MaxMotifLength: Int = 10
  private val MaxLength: Int      = 1000000
  private val MaxGcContents: Int  = 20

  def from(
      motif: DnaString,
      length: Int,
      gcContents: Vector[Probability]
  ): Either[ExpectedRestrictionSitesProblemError, ExpectedRestrictionSitesProblem] = {
    val motifLength = motif.value.length
    if (motifLength > MaxMotifLength)
      Left(ExpectedRestrictionSitesProblemError.MotifTooLong(motifLength, MaxMotifLength))
    else if (motifLength % 2 != 0)
      Left(ExpectedRestrictionSitesProblemError.OddMotifLength(motifLength))
    else if (length < 1)
      Left(ExpectedRestrictionSitesProblemError.NonPositiveLength(length))
    else if (length > MaxLength)
      Left(ExpectedRestrictionSitesProblemError.LengthTooLarge(length, MaxLength))
    else if (gcContents.size > MaxGcContents)
      Left(ExpectedRestrictionSitesProblemError.TooManyGcContents(gcContents.size, MaxGcContents))
    else
      Right(new ExpectedRestrictionSitesProblem(motif, length, gcContents) {})
  }
}
