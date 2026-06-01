package bio.domain.graph

/** The result of reconstructing a circular chromosome for the Rosalind PCOV
  * problem — a minimal-length cyclic superstring containing every read (see
  * [[bio.algorithms.graph.PerfectCoverageAssembly.assemble]]).
  *
  * `value` is one deterministic rotation of the circular chromosome; because the
  * string is circular, any rotation denotes the same chromosome. `format`
  * renders it as the bare symbol sequence, matching Rosalind's output.
  */
final case class CyclicSuperstring(value: String) {
  def format: String = value
}
