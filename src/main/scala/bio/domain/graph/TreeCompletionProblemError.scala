package bio.domain.graph

/** Construction failures for [[TreeCompletionProblem]]. */
sealed trait TreeCompletionProblemError
object TreeCompletionProblemError {

  /** The supplied node count `n` was less than 1. Carries the offending value. */
  final case class NonPositiveN(value: Int) extends TreeCompletionProblemError

  /** The supplied node count `n` exceeded the per-problem maximum `max`. Carries the
    * offending value and the maximum (so callers don't need to know the internal
    * constant).
    */
  final case class NExceedsMaximum(value: Int, max: Int) extends TreeCompletionProblemError

  /** An edge referenced a node that does not exist in `[1, n]`. Carries the full
    * offending edge and `n` so callers can pinpoint the violation.
    */
  final case class EdgeEndpointOutOfRange(edge: UndirectedEdge, n: Int)
      extends TreeCompletionProblemError
}
