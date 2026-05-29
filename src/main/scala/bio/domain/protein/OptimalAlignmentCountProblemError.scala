package bio.domain.protein

/** Construction failures for [[OptimalAlignmentCountProblem]]. */
sealed trait OptimalAlignmentCountProblemError
object OptimalAlignmentCountProblemError {

  /** The left-hand protein string exceeded the Rosalind CTEA cap of 1000 aa. */
  final case class LeftTooLong(length: Int, max: Int) extends OptimalAlignmentCountProblemError

  /** The right-hand protein string exceeded the Rosalind CTEA cap of 1000 aa. */
  final case class RightTooLong(length: Int, max: Int) extends OptimalAlignmentCountProblemError
}
