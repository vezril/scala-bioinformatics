package bio.domain.protein

/** Result of the Rosalind SGRA ("Using the Spectrum Graph to Infer Peptides") problem —
  * the longest protein matching the spectrum graph (see
  * [[bio.algorithms.protein.SpectrumGraph.longestPeptide]]).
  *
  * `format` returns the peptide string verbatim (the empty protein as the empty string).
  */
final case class SpectrumGraphPeptide(peptide: String) {

  def format: String = peptide
}
