package bio.domain.protein

/** Construction failures for [[AffineGapAlignmentProblem]] (Rosalind GAFF —
  * "Global Alignment with Scoring Matrix and Affine Gap Penalty").
  */
sealed trait AffineGapAlignmentProblemError
object AffineGapAlignmentProblemError {

  /** The left-hand protein string exceeded the Rosalind GAFF cap of 100 aa. */
  final case class LeftTooLong(length: Int, max: Int)
      extends AffineGapAlignmentProblemError

  /** The right-hand protein string exceeded the Rosalind GAFF cap of 100 aa. */
  final case class RightTooLong(length: Int, max: Int)
      extends AffineGapAlignmentProblemError
}
