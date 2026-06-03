package bio.domain.protein

/** Validation errors for [[SpectrumGraphProblem]] (Rosalind SGRA). */
sealed trait SpectrumGraphProblemError

object SpectrumGraphProblemError {

  /** The mass list exceeds the maximum allowed size. */
  final case class TooManyMasses(size: Int, max: Int) extends SpectrumGraphProblemError

  /** The mass at `index` is not a positive number. */
  final case class NonPositiveMass(index: Int, value: Double) extends SpectrumGraphProblemError
}
