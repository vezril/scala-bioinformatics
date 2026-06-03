package bio.domain.analysis

/** Result of the Rosalind SCSP ("Interleaving Two Motifs") problem — a shortest common
  * supersequence of the two input strings (see
  * [[bio.algorithms.analysis.ShortestCommonSupersequence.build]]).
  *
  * `format` returns the supersequence string verbatim.
  */
final case class Supersequence(value: String) {

  def format: String = value
}
