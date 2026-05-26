package bio.domain.genetics

/** Parameters for the independent-alleles binomial-tail problem.
  *
  * Models the family tree rooted at Tom (Aa Bb in generation 0) where every organism
  * has two children, each mating with an Aa Bb partner. Generation `k` contains
  * `2^k` organisms (`populationSize`), and we ask: what is the probability that at
  * least `atLeast` of those organisms carry the genotype Aa Bb?
  *
  * Constructable only via [[IndependentAllelesProblem.from]] which enforces:
  *   - `generations >= 1`
  *   - `atLeast >= 1`
  *   - `atLeast <= 2^generations`
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  *
  * For Rosalind's `k ≤ 7`, `populationSize` stays at or below `128`. Larger `generations`
  * values are accepted by validation but may exceed `Int.MaxValue` for the bit-shift in
  * downstream algorithms; the framework's current `IndependentAlleles.probability`
  * assumes `populationSize` fits an `Int`.
  */
sealed abstract case class IndependentAllelesProblem(generations: Int, atLeast: Int) {
  def populationSize: Long = 1L << generations
}

object IndependentAllelesProblem {
  def from(
      generations: Int,
      atLeast: Int
  ): Either[IndependentAllelesProblemError, IndependentAllelesProblem] =
    if (generations < 1)
      Left(IndependentAllelesProblemError.NonPositiveGenerations(generations))
    else if (atLeast < 1)
      Left(IndependentAllelesProblemError.NonPositiveAtLeast(atLeast))
    else if (atLeast.toLong > (1L << generations))
      Left(IndependentAllelesProblemError.AtLeastExceedsPopulation(atLeast, generations))
    else
      Right(new IndependentAllelesProblem(generations, atLeast) {})
}
