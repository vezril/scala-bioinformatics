package bio.algorithms.genetics

import bio.domain.genetics.{Genotype, GenotypeProbabilities, Pedigree, PedigreeProblem}

/** Infers the genotype probability distribution of the root individual of a pedigree —
  * Rosalind MEND ("Inferring Genotype from a Pedigree").
  *
  * Each node carries a distribution `(P(AA), P(Aa), P(aa))`. A known ancestor is a point
  * mass on its genotype. For an offspring, the probability a parent transmits the `A`
  * allele is `tA = P(AA) + P(Aa)/2`; since the two parents transmit independently, the
  * offspring distribution is `P(AA) = tA₁·tA₂`, `P(aa) = (1−tA₁)(1−tA₂)`, and `P(Aa)`
  * the remainder. This closed form equals the full Mendelian cross convolution.
  *
  * Pure and total: a recursion over the immutable `Pedigree` ADT, no `var`/`while`/
  * mutable state.
  */
object InferGenotype {

  def infer(problem: PedigreeProblem): GenotypeProbabilities = {
    val (aa, ab, bb) = distribution(problem.pedigree)
    GenotypeProbabilities(aa, ab, bb)
  }

  /** The `(P(AA), P(Aa), P(aa))` distribution of a pedigree node. */
  private def distribution(node: Pedigree): (Double, Double, Double) =
    node match {
      case Pedigree.KnownAncestor(Genotype.HomozygousDominant)  => (1.0, 0.0, 0.0)
      case Pedigree.KnownAncestor(Genotype.Heterozygous)        => (0.0, 1.0, 0.0)
      case Pedigree.KnownAncestor(Genotype.HomozygousRecessive) => (0.0, 0.0, 1.0)
      case Pedigree.Offspring(parent1, parent2) =>
        val tA1 = transmitA(distribution(parent1))
        val tA2 = transmitA(distribution(parent2))
        val pAA = tA1 * tA2
        val paa = (1.0 - tA1) * (1.0 - tA2)
        (pAA, 1.0 - pAA - paa, paa)
    }

  /** Probability a parent with the given distribution transmits the `A` allele. */
  private def transmitA(dist: (Double, Double, Double)): Double =
    dist._1 + dist._2 / 2.0
}
