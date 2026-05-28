package bio.domain.analysis

/** Construction failures for [[SplicedMotifProblem]]. */
sealed trait SplicedMotifProblemError
object SplicedMotifProblemError {

  /** The source DNA string exceeded the Rosalind SSEQ cap of 1000 nt. */
  final case class SourceTooLong(length: Int, max: Int) extends SplicedMotifProblemError

  /** The target DNA string (the subsequence to find) exceeded the Rosalind SSEQ
    * cap of 1000 nt.
    */
  final case class TargetTooLong(length: Int, max: Int) extends SplicedMotifProblemError
}
