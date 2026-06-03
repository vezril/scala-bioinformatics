package bio.domain.protein

/** Validation errors for [[FullSpectrumProblem]] (Rosalind FULL). */
sealed trait FullSpectrumProblemError

object FullSpectrumProblemError {

  /** The mass list size is not `2n+3` for any `n >= 1` (i.e. not odd and at least 5). */
  final case class InvalidSize(size: Int) extends FullSpectrumProblemError

  /** The mass at `index` is not a positive number. */
  final case class NonPositiveMass(index: Int, value: Double) extends FullSpectrumProblemError
}
