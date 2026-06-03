package bio.domain.protein

/** Validated input for the Rosalind SGRA ("Using the Spectrum Graph to Infer Peptides")
  * problem — see [[bio.algorithms.protein.SpectrumGraph.longestPeptide]].
  *
  * Wraps the mass list `L` (the spectrum-graph nodes). The smart constructor enforces,
  * first failure wins:
  *   1. `masses.size <= 100` (Rosalind cap), else `TooManyMasses`;
  *   2. every mass is positive, else `NonPositiveMass`.
  *
  * The empty list is accepted (no nodes yield the empty protein).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[SpectrumGraphProblem.from]].
  */
sealed abstract case class SpectrumGraphProblem(masses: Vector[Double])

object SpectrumGraphProblem {
  private val MaxMasses: Int = 100

  def from(masses: Vector[Double]): Either[SpectrumGraphProblemError, SpectrumGraphProblem] =
    if (masses.size > MaxMasses)
      Left(SpectrumGraphProblemError.TooManyMasses(masses.size, MaxMasses))
    else
      masses.iterator.zipWithIndex.collectFirst {
        case (value, index) if value <= 0.0 =>
          SpectrumGraphProblemError.NonPositiveMass(index, value)
      } match {
        case Some(err) => Left(err)
        case None      => Right(new SpectrumGraphProblem(masses) {})
      }
}
