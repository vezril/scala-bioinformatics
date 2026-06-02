package bio.domain.analysis

/** Construction failures for [[SemiglobalAlignmentProblem]] (Rosalind SMGB —
  * "Semiglobal Alignment").
  */
sealed trait SemiglobalAlignmentProblemError
object SemiglobalAlignmentProblemError {

  /** The first string `s` exceeded the Rosalind SMGB cap of 10 000 nt. */
  final case class STooLong(length: Int, max: Int)
      extends SemiglobalAlignmentProblemError

  /** The second string `t` exceeded the Rosalind SMGB cap of 10 000 nt. */
  final case class TTooLong(length: Int, max: Int)
      extends SemiglobalAlignmentProblemError
}
