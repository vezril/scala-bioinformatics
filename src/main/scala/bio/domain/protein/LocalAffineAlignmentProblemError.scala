package bio.domain.protein

/** Construction failures for [[LocalAffineAlignmentProblem]] (Rosalind LAFF —
  * "Local Alignment with Affine Gap Penalty").
  */
sealed trait LocalAffineAlignmentProblemError
object LocalAffineAlignmentProblemError {

  /** The left-hand protein string exceeded the Rosalind LAFF cap of 10,000 aa. */
  final case class LeftTooLong(length: Int, max: Int)
      extends LocalAffineAlignmentProblemError

  /** The right-hand protein string exceeded the Rosalind LAFF cap of 10,000 aa. */
  final case class RightTooLong(length: Int, max: Int)
      extends LocalAffineAlignmentProblemError
}
