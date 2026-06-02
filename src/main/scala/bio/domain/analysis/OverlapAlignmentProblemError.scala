package bio.domain.analysis

/** Construction failures for [[OverlapAlignmentProblem]] (Rosalind OAP —
  * "Overlap Alignment").
  */
sealed trait OverlapAlignmentProblemError
object OverlapAlignmentProblemError {

  /** The first string `s` exceeded the Rosalind OAP cap of 10 000 nt. */
  final case class STooLong(length: Int, max: Int)
      extends OverlapAlignmentProblemError

  /** The second string `t` exceeded the Rosalind OAP cap of 10 000 nt. */
  final case class TTooLong(length: Int, max: Int)
      extends OverlapAlignmentProblemError
}
