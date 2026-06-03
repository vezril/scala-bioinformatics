package bio.domain.protein

/** Validated input for the Rosalind PRSM ("Matching a Spectrum to a Protein") problem
  * — see [[bio.algorithms.protein.MatchSpectrum.bestMatch]].
  *
  * Wraps the candidate proteins and the target spectrum `R` (a multiset of positive
  * numbers). The smart constructor enforces, first failure wins:
  *   1. `proteins.nonEmpty`, else `EmptyProteinList`;
  *   2. `spectrum.nonEmpty`, else `EmptySpectrum`;
  *   3. every spectrum value is positive, else `NonPositiveMass(index, value)`.
  *
  * Protein-character validity is owned upstream by `ProteinString`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[SpectrumMatchProblem.from]].
  */
sealed abstract case class SpectrumMatchProblem(
    proteins: Vector[ProteinString],
    spectrum: Vector[Double]
)

object SpectrumMatchProblem {

  def from(
      proteins: Vector[ProteinString],
      spectrum: Vector[Double]
  ): Either[SpectrumMatchProblemError, SpectrumMatchProblem] =
    if (proteins.isEmpty)
      Left(SpectrumMatchProblemError.EmptyProteinList)
    else if (spectrum.isEmpty)
      Left(SpectrumMatchProblemError.EmptySpectrum)
    else
      spectrum.iterator.zipWithIndex.collectFirst {
        case (value, index) if value <= 0.0 =>
          SpectrumMatchProblemError.NonPositiveMass(index, value)
      } match {
        case Some(err) => Left(err)
        case None      => Right(new SpectrumMatchProblem(proteins, spectrum) {})
      }
}
