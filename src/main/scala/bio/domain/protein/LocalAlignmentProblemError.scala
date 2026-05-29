package bio.domain.protein

/** Construction failures for [[LocalAlignmentProblem]]. */
sealed trait LocalAlignmentProblemError
object LocalAlignmentProblemError {

  /** The left-hand protein string exceeded the Rosalind LOCA cap of 1000 aa. */
  final case class LeftTooLong(length: Int, max: Int) extends LocalAlignmentProblemError

  /** The right-hand protein string exceeded the Rosalind LOCA cap of 1000 aa. */
  final case class RightTooLong(length: Int, max: Int) extends LocalAlignmentProblemError
}
