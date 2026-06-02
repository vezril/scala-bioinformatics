package bio.domain.protein

/** A validated multiset of positive real masses — one of the two input spectra of
  * the Rosalind CONV ("Comparing Spectra with the Spectral Convolution") problem.
  *
  * Wraps a `Vector[Double]` so that order and repeated masses are preserved (a
  * multiset, not a set). The smart constructor enforces, first-failure-wins:
  *   1. the vector is non-empty, else `EmptyMultiset`;
  *   2. `masses.length <= 200`, else `TooManyMasses`;
  *   3. every mass is positive, else `NonPositiveMass` at the first offender.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[MassMultiset.from]].
  */
sealed abstract case class MassMultiset(masses: Vector[Double])

object MassMultiset {
  private val MaxSize: Int = 200

  def from(masses: Vector[Double]): Either[MassMultisetError, MassMultiset] =
    if (masses.isEmpty)
      Left(MassMultisetError.EmptyMultiset)
    else if (masses.length > MaxSize)
      Left(MassMultisetError.TooManyMasses(masses.length, MaxSize))
    else
      masses.zipWithIndex.collectFirst {
        case (m, i) if m <= 0.0 => MassMultisetError.NonPositiveMass(i, m)
      } match {
        case Some(err) => Left(err)
        case None      => Right(new MassMultiset(masses) {})
      }
}
