package bio.domain.protein

/** Result of the Rosalind CONV ("Comparing Spectra with the Spectral Convolution")
  * problem — the largest multiplicity of the spectral convolution `S₁⊖S₂` together
  * with a shift value `x` that achieves it.
  *
  * @param multiplicity the largest number of times any single shift recurs
  * @param shift        a difference `s₁ − s₂` achieving that multiplicity (may be
  *                     negative; the rendering reports its absolute value)
  */
final case class SpectralConvolution(multiplicity: Int, shift: Double) {

  /** Rosalind CONV rendering: the multiplicity on the first line, then the
    * absolute value of the maximizing shift to five decimal places.
    */
  def format: String = s"$multiplicity\n${f"${math.abs(shift)}%.5f"}"
}
