package bio.domain.graph

/** One directed edge in a de Bruijn graph `B_k`: an edge `from -> to` exists
  * because some (k+1)-mer `r` has length-`k` prefix `from` and length-`k`
  * suffix `to`.
  *
  * `from` and `to` are the k-mer strings themselves (`String`), matching the
  * Rosalind DBRU adjacency-list output format.
  *
  * Public constructor — `DeBruijnEdge` carries no invariant beyond holding two
  * strings, so no smart-constructor ceremony is needed (mirrors [[OverlapEdge]]).
  */
final case class DeBruijnEdge(from: String, to: String)
