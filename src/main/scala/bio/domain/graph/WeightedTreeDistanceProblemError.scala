package bio.domain.graph

/** Validation errors for [[WeightedTreeDistanceProblem]] (Rosalind NKEW). */
sealed trait WeightedTreeDistanceProblemError

object WeightedTreeDistanceProblemError {

  /** A queried node label does not occur in the tree. */
  final case class NodeNotFound(label: String) extends WeightedTreeDistanceProblemError
}
