package bio.domain.protein

/** Construction failures for [[PrefixSpectrum]] (Rosalind SPEC — "Inferring
  * Protein from Spectrum").
  */
sealed trait PrefixSpectrumError
object PrefixSpectrumError {

  /** The weight list was empty; a length-`n − 1` protein needs `n ≥ 1`. */
  case object EmptySpectrum extends PrefixSpectrumError

  /** The weight list exceeded the Rosalind SPEC cap of 100 weights. */
  final case class TooManyWeights(count: Int, max: Int) extends PrefixSpectrumError

  /** A prefix weight at `index` was not a positive real (`value <= 0`). */
  final case class NonPositiveWeight(index: Int, value: Double)
      extends PrefixSpectrumError
}
