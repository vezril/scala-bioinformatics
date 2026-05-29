package bio.domain.protein

/** Construction failures for [[GlobalAlignmentScoreProblem]]. */
sealed trait GlobalAlignmentScoreProblemError
object GlobalAlignmentScoreProblemError {

  /** The left-hand protein string exceeded the Rosalind GLOB cap of 1000 aa. */
  final case class LeftTooLong(length: Int, max: Int) extends GlobalAlignmentScoreProblemError

  /** The right-hand protein string exceeded the Rosalind GLOB cap of 1000 aa. */
  final case class RightTooLong(length: Int, max: Int) extends GlobalAlignmentScoreProblemError
}
