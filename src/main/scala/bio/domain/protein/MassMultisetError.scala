package bio.domain.protein

/** Construction failures for [[MassMultiset]] (Rosalind CONV — "Comparing Spectra
  * with the Spectral Convolution").
  */
sealed trait MassMultisetError
object MassMultisetError {

  /** The mass list was empty; a spectrum must contain at least one mass. */
  case object EmptyMultiset extends MassMultisetError

  /** The mass list exceeded the Rosalind CONV cap of 200 masses. */
  final case class TooManyMasses(size: Int, max: Int) extends MassMultisetError

  /** A mass at `index` was not a positive real (`value <= 0`). */
  final case class NonPositiveMass(index: Int, value: Double)
      extends MassMultisetError
}
