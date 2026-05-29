package bio.domain.analysis

/** Construction failures for [[FittingAlignmentProblem]] (Rosalind SIMS —
  * "Finding a Motif with Modifications").
  */
sealed trait FittingAlignmentProblemError
object FittingAlignmentProblemError {

  /** The text string `s` exceeded the Rosalind SIMS cap of 10 000 nt. */
  final case class TextTooLong(length: Int, max: Int)
      extends FittingAlignmentProblemError

  /** The motif string `t` exceeded the Rosalind SIMS cap of 1 000 nt. */
  final case class MotifTooLong(length: Int, max: Int)
      extends FittingAlignmentProblemError
}
