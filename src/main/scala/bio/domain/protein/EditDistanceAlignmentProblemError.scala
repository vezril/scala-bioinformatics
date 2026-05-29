package bio.domain.protein

/** Construction failures for [[EditDistanceAlignmentProblem]]. */
sealed trait EditDistanceAlignmentProblemError
object EditDistanceAlignmentProblemError {

  /** The left-hand protein string exceeded the Rosalind EDTA cap of 1000 aa. */
  final case class LeftTooLong(length: Int, max: Int) extends EditDistanceAlignmentProblemError

  /** The right-hand protein string exceeded the Rosalind EDTA cap of 1000 aa. */
  final case class RightTooLong(length: Int, max: Int) extends EditDistanceAlignmentProblemError
}
