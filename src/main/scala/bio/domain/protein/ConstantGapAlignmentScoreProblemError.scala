package bio.domain.protein

/** Construction failures for [[ConstantGapAlignmentScoreProblem]] (Rosalind
  * GCON — "Global Alignment with Constant Gap Penalty").
  */
sealed trait ConstantGapAlignmentScoreProblemError
object ConstantGapAlignmentScoreProblemError {

  /** The left-hand protein string exceeded the Rosalind GCON cap of 1000 aa. */
  final case class LeftTooLong(length: Int, max: Int)
      extends ConstantGapAlignmentScoreProblemError

  /** The right-hand protein string exceeded the Rosalind GCON cap of 1000 aa. */
  final case class RightTooLong(length: Int, max: Int)
      extends ConstantGapAlignmentScoreProblemError
}
