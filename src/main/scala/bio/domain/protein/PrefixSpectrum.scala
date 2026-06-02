package bio.domain.protein

/** Validated input for the Rosalind SPEC ("Inferring Protein from Spectrum")
  * problem — see [[bio.algorithms.protein.InferProteinFromSpectrum.infer]].
  *
  * Wraps the list `L` of **prefix weights** (the cumulative weight of every prefix
  * of some weighted protein). Consecutive differences are residue masses, so a
  * spectrum of `n` weights reconstructs a protein of length `n − 1`.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. the list is non-empty, else `EmptySpectrum`;
  *   2. `weights.length <= 100`, else `TooManyWeights`;
  *   3. every weight is positive, else `NonPositiveWeight` at the first offender.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[PrefixSpectrum.from]].
  */
sealed abstract case class PrefixSpectrum(weights: Vector[Double])

object PrefixSpectrum {
  private val MaxWeights: Int = 100

  def from(
      weights: Vector[Double]
  ): Either[PrefixSpectrumError, PrefixSpectrum] =
    if (weights.isEmpty)
      Left(PrefixSpectrumError.EmptySpectrum)
    else if (weights.length > MaxWeights)
      Left(PrefixSpectrumError.TooManyWeights(weights.length, MaxWeights))
    else
      weights.zipWithIndex.collectFirst {
        case (w, i) if w <= 0.0 => PrefixSpectrumError.NonPositiveWeight(i, w)
      } match {
        case Some(err) => Left(err)
        case None      => Right(new PrefixSpectrum(weights) {})
      }
}
