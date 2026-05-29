package bio.domain.protein

/** Construction failures for [[EditDistanceProblem]]. */
sealed trait EditDistanceProblemError
object EditDistanceProblemError {

  /** The left-hand protein string exceeded the Rosalind EDIT cap of 1000 aa. */
  final case class LeftTooLong(length: Int, max: Int) extends EditDistanceProblemError

  /** The right-hand protein string exceeded the Rosalind EDIT cap of 1000 aa. */
  final case class RightTooLong(length: Int, max: Int) extends EditDistanceProblemError
}
