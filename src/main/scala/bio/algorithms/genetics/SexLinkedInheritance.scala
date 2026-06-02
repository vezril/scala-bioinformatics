package bio.algorithms.genetics

import bio.domain.genetics.{CarrierProbabilities, SexLinkedProblem}

/** Computes, for each recessive X-linked gene, the probability that a random female is
  * a carrier — Rosalind SEXL ("Sex-Linked Inheritance").
  *
  * A male is hemizygous, so the proportion of males exhibiting the trait equals the
  * recessive allele frequency `q`. Under Hardy–Weinberg equilibrium a female is a
  * carrier (heterozygous) with probability `2·q·(1 − q)`.
  *
  * Pure and total: a single `map` over the immutable proportions, preserving order. No
  * `var`/`while`/mutable collection.
  */
object SexLinkedInheritance {

  def carrierProbabilities(problem: SexLinkedProblem): CarrierProbabilities =
    CarrierProbabilities(problem.maleProportions.map { p =>
      val q = p.value
      2.0 * q * (1.0 - q)
    })
}
