package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Input for the Rosalind LING ("Linguistic Complexity of a Genome") problem — see
  * [[bio.algorithms.analysis.LinguisticComplexityAnalysis.compute]].
  *
  * Wraps the DNA string `s` as a [[DnaString]]. The Rosalind 100 kbp bound is already
  * guaranteed by `DnaString` (whose own maximum length is 100000 bp), so this type
  * carries no additional invariant and is a plain wrapper (mirroring
  * `SpectralConvolutionProblem`).
  */
final case class LinguisticComplexityProblem(dna: DnaString)
