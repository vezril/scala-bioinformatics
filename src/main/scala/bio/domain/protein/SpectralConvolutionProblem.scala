package bio.domain.protein

/** Validated input for the Rosalind CONV ("Comparing Spectra with the Spectral
  * Convolution") problem — see
  * [[bio.algorithms.protein.SpectralConvolution.convolve]].
  *
  * Bundles the two mass multisets being compared: the minuend `s1` and the
  * subtrahend `s2`. The spectral convolution `S₁⊖S₂` is the multiset of all
  * differences `s₁ − s₂`. Both multisets are already validated by
  * [[MassMultiset]], so this bundle carries no further invariant.
  */
final case class SpectralConvolutionProblem(s1: MassMultiset, s2: MassMultiset)
