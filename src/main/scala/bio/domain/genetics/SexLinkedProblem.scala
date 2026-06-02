package bio.domain.genetics

import bio.domain.stats.Probability

/** Input for the Rosalind SEXL ("Sex-Linked Inheritance") problem — see
  * [[bio.algorithms.genetics.SexLinkedInheritance.carrierProbabilities]].
  *
  * Wraps the male trait proportions: `maleProportions(k)` is the proportion of males
  * exhibiting the `k`-th recessive X-linked gene (equivalently, that gene's recessive
  * allele frequency). Each proportion's `[0,1]` bound is owned by [[Probability]], so
  * this type carries no additional invariant and is a plain wrapper (mirroring
  * `SpectralConvolutionProblem`).
  */
final case class SexLinkedProblem(maleProportions: Vector[Probability])
