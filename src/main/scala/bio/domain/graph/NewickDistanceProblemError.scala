package bio.domain.graph

/** Construction failures for [[NewickDistanceProblem]]. */
sealed trait NewickDistanceProblemError
object NewickDistanceProblemError {

  /** A query endpoint label was not present in the tree's label set. */
  final case class UnknownLabel(label: String) extends NewickDistanceProblemError
}
