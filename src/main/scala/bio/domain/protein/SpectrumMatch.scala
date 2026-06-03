package bio.domain.protein

/** Result of the Rosalind PRSM ("Matching a Spectrum to a Protein") problem — the
  * maximum multiplicity of `R⊖S[s_k]` and the candidate protein achieving it (see
  * [[bio.algorithms.protein.MatchSpectrum.bestMatch]]).
  *
  * `format` renders the multiplicity on the first line and the protein on the second.
  */
final case class SpectrumMatch(multiplicity: Int, protein: String) {

  def format: String = s"$multiplicity\n$protein"
}
