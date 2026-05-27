package bio.domain.graph

/** Construction failures for [[UndirectedEdge]]. */
sealed trait UndirectedEdgeError
object UndirectedEdgeError {

  /** The two endpoints were equal (a self-loop). Carries the offending node label. */
  final case class SelfLoop(node: Int) extends UndirectedEdgeError

  /** The `u` endpoint was less than 1 (node labels are 1-indexed). Carries the
    * offending value.
    */
  final case class NonPositiveU(value: Int) extends UndirectedEdgeError

  /** The `v` endpoint was less than 1 (node labels are 1-indexed). Carries the
    * offending value.
    */
  final case class NonPositiveV(value: Int) extends UndirectedEdgeError
}
