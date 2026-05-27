package bio.domain.analysis

/** One directed edge in an overlap graph: there is an edge `from -> to` because a
  * length-`k` suffix of the source sequence equals a length-`k` prefix of the target
  * sequence.
  *
  * `from` and `to` are FASTA record ids (`String`), not references to the records
  * themselves — once the overlap graph has been computed, the sequence data is no
  * longer needed downstream and matching the Rosalind output format (id-per-side) is
  * the natural representation.
  *
  * Public constructor — `OverlapEdge` carries no invariant beyond holding two strings,
  * so no smart-constructor ceremony is needed (mirrors [[bio.parsing.FastaRecord]]).
  */
final case class OverlapEdge(from: String, to: String)
