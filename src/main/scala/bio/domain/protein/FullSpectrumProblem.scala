package bio.domain.protein

/** Validated input for the Rosalind FULL ("Inferring Peptide from Full Spectrum")
  * problem — see [[bio.algorithms.protein.InferPeptide.infer]].
  *
  * Wraps the mass list `L`: `masses(0)` is the parent mass and the remaining `2n+2`
  * values are the b-ions and y-ions of a length-`n` peptide. The smart constructor
  * enforces, first failure wins:
  *   1. `masses.size` is `2n+3` for some `n >= 1` — i.e. odd and at least 5 — else `InvalidSize`;
  *   2. every mass is positive, else `NonPositiveMass`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[FullSpectrumProblem.from]].
  */
sealed abstract case class FullSpectrumProblem(masses: Vector[Double])

object FullSpectrumProblem {
  private val MinSize: Int = 5

  def from(masses: Vector[Double]): Either[FullSpectrumProblemError, FullSpectrumProblem] =
    if (masses.size < MinSize || masses.size % 2 == 0)
      Left(FullSpectrumProblemError.InvalidSize(masses.size))
    else
      masses.iterator.zipWithIndex.collectFirst {
        case (value, index) if value <= 0.0 =>
          FullSpectrumProblemError.NonPositiveMass(index, value)
      } match {
        case Some(err) => Left(err)
        case None      => Right(new FullSpectrumProblem(masses) {})
      }
}
