package bio.domain.protein

/** Validation errors for [[SpectrumMatchProblem]] (Rosalind PRSM). */
sealed trait SpectrumMatchProblemError

object SpectrumMatchProblemError {

  /** No candidate proteins were supplied. */
  case object EmptyProteinList extends SpectrumMatchProblemError

  /** The target spectrum is empty. */
  case object EmptySpectrum extends SpectrumMatchProblemError

  /** The spectrum value at `index` is not a positive number. */
  final case class NonPositiveMass(index: Int, value: Double) extends SpectrumMatchProblemError
}
