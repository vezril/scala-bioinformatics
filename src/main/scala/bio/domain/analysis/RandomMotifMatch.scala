package bio.domain.analysis

/** Result of the Rosalind RSTR ("Matching Random Motifs") problem — the probability
  * that at least one of `N` random DNA strings (each the length of the motif,
  * generated with the given GC-content) equals the motif.
  */
final case class RandomMotifMatch(probability: Double) {

  /** Rosalind RSTR rendering: the probability rounded to three decimal places. */
  def format: String = f"$probability%.3f"
}
