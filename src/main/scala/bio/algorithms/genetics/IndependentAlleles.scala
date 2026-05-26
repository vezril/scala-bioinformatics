package bio.algorithms.genetics

import bio.domain.genetics.IndependentAllelesProblem
import bio.domain.stats.Probability

/** Computes the binomial-tail probability for Tom's family tree (Aa Bb in generation 0,
  * every organism mating with Aa Bb, two children per organism).
  *
  * Key independence fact: regardless of any descendant's genotype, when crossed with an
  * Aa Bb mate the offspring is Aa Bb with probability exactly `1/4`. Therefore generation
  * `k` contains `2^k` organisms each independently Aa Bb with `p = 1/4`, and the question
  * "what is the probability that at least N of them are Aa Bb?" is the binomial tail
  * `P(X >= N)` for `X ~ Binomial(2^k, 1/4)`.
  *
  * Algorithm: walk the PMF from `P(X = 0) = q^total` using the ratio recurrence
  * `P(X = j+1) = P(X = j) * (total - j) / (j + 1) * p/q`, then sum the entries from
  * index `atLeast` to `total`. Avoids factorials; numerically stable for Rosalind's
  * `k ≤ 7` (population ≤ 128).
  */
object IndependentAlleles {

  private val P: Double     = 0.25
  private val Q: Double     = 0.75
  private val Ratio: Double = P / Q

  def probability(problem: IndependentAllelesProblem): Probability = {
    val total: Int = problem.populationSize.toInt
    val n: Int     = problem.atLeast
    val pmf0: Double = math.pow(Q, total.toDouble)
    val pmfs: IndexedSeq[Double] = (0 until total).scanLeft(pmf0) { (pmfJ, j) =>
      pmfJ * (total - j).toDouble / (j + 1).toDouble * Ratio
    }
    Probability.unsafeFrom(pmfs.drop(n).sum)
  }
}
