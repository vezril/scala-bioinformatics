package bio.domain.protein

/** Result of the Rosalind FULL ("Inferring Peptide from Full Spectrum") problem — the
  * reconstructed peptide (see [[bio.algorithms.protein.InferPeptide.infer]]).
  *
  * `format` returns the peptide string verbatim.
  */
final case class InferredPeptide(peptide: String) {

  def format: String = peptide
}
