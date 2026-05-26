package bio.algorithms.genetics

import bio.domain.genetics.Population
import bio.domain.stats.Probability

object MendelianInheritance {

  /** Returns the probability that two uniformly randomly selected distinct mating organisms
    * from the given [[Population]] produce offspring carrying at least one dominant allele
    * (i.e., displaying the dominant phenotype).
    *
    * Closed-form derivation: enumerate ordered pairs of distinct parents and condition on
    * the offspring being homozygous recessive (aa). The result is `1 - P(aa)` where
    *   P(aa) = [ m(m-1)/4 + m*n + n(n-1) ] / (N * (N-1))
    * with `k`, `m`, `n` the homozygous-dominant, heterozygous, and homozygous-recessive
    * counts, and `N = k + m + n`.
    *
    * Totality: `Population.from` guarantees `N >= 2`, so the denominator is non-zero.
    * The numerator is non-negative, so `P(aa) ∈ [0, 1]` and therefore `1 - P(aa) ∈ [0, 1]`.
    */
  def probabilityOfDominantPhenotype(pop: Population): Probability = {
    val m = pop.heterozygous.toDouble
    val n = pop.homozygousRecessive.toDouble
    val total = pop.total.toDouble
    val recessiveNumerator = m * (m - 1.0) / 4.0 + m * n + n * (n - 1.0)
    val denominator = total * (total - 1.0)
    val pRecessive = recessiveNumerator / denominator
    Probability.unsafeFrom(1.0 - pRecessive)
  }
}
