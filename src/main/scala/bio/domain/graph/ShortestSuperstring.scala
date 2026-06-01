package bio.domain.graph

/** Result of assembling a [[GenomeAssemblyProblem]] — the shortest superstring
  * that contains every input read as a substring (Rosalind LONG).
  *
  * `value` is the assembled chromosome sequence; [[format]] renders it for output.
  */
final case class ShortestSuperstring(value: String) {
  def format: String = value
}
