package bio.domain.analysis

/** Construction failures for [[RandomMatchProblem]]. */
sealed trait RandomMatchProblemError
object RandomMatchProblemError {

  /** The supplied DNA exceeded the per-problem maximum length (`max`). Carries the
    * observed `length` so callers can report it without re-measuring.
    */
  final case class DnaTooLong(length: Int, max: Int) extends RandomMatchProblemError

  /** The supplied GC-content array exceeded the per-problem maximum size (`max`).
    * Carries the observed `size` so callers can report it without re-measuring.
    */
  final case class TooManyGcContents(size: Int, max: Int) extends RandomMatchProblemError
}
