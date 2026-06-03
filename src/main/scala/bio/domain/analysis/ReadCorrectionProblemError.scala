package bio.domain.analysis

/** Validation errors for [[ReadCorrectionProblem]] (Rosalind CORR). */
sealed trait ReadCorrectionProblemError

object ReadCorrectionProblemError {

  /** More than the allowed number of reads was supplied. */
  final case class TooManyReads(count: Int, max: Int) extends ReadCorrectionProblemError

  /** One of the reads exceeded the maximum allowed length. */
  final case class ReadTooLong(length: Int, max: Int) extends ReadCorrectionProblemError

  /** The reads are not all of equal length (carries every length). */
  final case class UnequalLengths(lengths: Vector[Int]) extends ReadCorrectionProblemError
}
