package bio.domain.analysis

/** Validation errors for [[InterwovenMotifProblem]] (Rosalind ITWV). */
sealed trait InterwovenMotifProblemError

object InterwovenMotifProblemError {

  /** More than the allowed number of pattern strings was supplied. */
  final case class TooManyPatterns(count: Int, max: Int) extends InterwovenMotifProblemError

  /** The text DNA string exceeded the maximum allowed length. */
  final case class TextTooLong(length: Int, max: Int) extends InterwovenMotifProblemError

  /** One of the pattern strings exceeded the maximum allowed length. */
  final case class PatternTooLong(length: Int, max: Int) extends InterwovenMotifProblemError
}
