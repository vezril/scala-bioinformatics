package bio.domain.matrix

import bio.domain.nucleic.DnaString

/** The output of the consensus-and-profile algorithm: a consensus DNA string paired
  * with its source profile matrix.
  *
  * Both fields are already validated by their own types — `DnaString` enforces the
  * `{A, C, G, T}` alphabet (and length cap), `ProfileMatrix` enforces the 4-key /
  * equal-column-length invariant. `ConsensusProfileResult` carries no further
  * invariant, so it's a plain `final case class` (mirrors [[bio.parsing.FastaRecord]]
  * and [[bio.domain.analysis.OverlapEdge]]).
  */
final case class ConsensusProfileResult(consensus: DnaString, profile: ProfileMatrix)
