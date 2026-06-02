package bio.domain.graph

/** The result of the Rosalind GREP problem ("Genome Assembly with Perfect Coverage
  * and Repeats") — every circular string assembled by a complete cycle in the de
  * Bruijn graph of the reads (see
  * [[bio.algorithms.graph.CompleteCycleAssembly.assemble]]).
  *
  * `strings` holds the distinct circular strings; each is one rotation-fixed
  * assembly that begins with the first input read. `format` renders them one per
  * line, matching Rosalind's output (which accepts the strings in any order).
  */
final case class CompleteCycleAssemblies(strings: Vector[String]) {
  def format: String = strings.mkString("\n")
}
