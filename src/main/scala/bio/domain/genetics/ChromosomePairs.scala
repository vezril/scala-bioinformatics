package bio.domain.genetics

/** The haploid chromosome-pair count `n` for a diploid genome: the total number of
  * chromosomes being modeled by [[bio.algorithms.genetics.IndependentSegregation]]
  * is `2 * value` (each pair contributes one chromosome from each parent).
  *
  * Constructable only via [[ChromosomePairs.from]] which enforces:
  *   - `value >= 1` (Rosalind: "positive integer")
  *   - `value <= 50` (Rosalind upper bound)
  *
  * Validation order: lower bound, then upper bound. First failure wins.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor. Mirrors the framework's existing
  * single-parameter validated wrappers (`PermutationLength`, `SubsetUniverseSize`,
  * `OverlapLength`).
  */
sealed abstract case class ChromosomePairs(value: Int)

object ChromosomePairs {
  private val MaxN: Int = 50

  def from(value: Int): Either[ChromosomePairsError, ChromosomePairs] =
    if (value < 1) Left(ChromosomePairsError.NonPositive(value))
    else if (value > MaxN) Left(ChromosomePairsError.ExceedsMaximum(value, MaxN))
    else Right(new ChromosomePairs(value) {})
}
