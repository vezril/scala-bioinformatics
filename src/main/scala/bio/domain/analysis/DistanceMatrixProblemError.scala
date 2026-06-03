package bio.domain.analysis

/** Validation errors for [[DistanceMatrixProblem]] (Rosalind PDST). */
sealed trait DistanceMatrixProblemError

object DistanceMatrixProblemError {

  /** More than the allowed number of strings was supplied. */
  final case class TooManyStrings(count: Int, max: Int) extends DistanceMatrixProblemError

  /** One of the strings exceeded the maximum allowed length. */
  final case class StringTooLong(length: Int, max: Int) extends DistanceMatrixProblemError

  /** The strings are not all of equal length (carries every length). */
  final case class UnequalLengths(lengths: Vector[Int]) extends DistanceMatrixProblemError
}
