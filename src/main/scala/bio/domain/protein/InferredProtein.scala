package bio.domain.protein

/** Result of the Rosalind SPEC ("Inferring Protein from Spectrum") problem — the
  * protein reconstructed from a [[PrefixSpectrum]].
  *
  * For a spectrum of `n` weights the protein has length `n − 1` (one residue per
  * consecutive-weight difference).
  */
final case class InferredProtein(protein: ProteinString) {

  /** Rosalind SPEC rendering: the protein's single-letter codes on one line. */
  def format: String = protein.value
}
