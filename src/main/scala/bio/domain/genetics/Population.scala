package bio.domain.genetics

/** Typed counts of organisms in a Mendelian population.
  * Constructable only via [[Population.from]] which enforces:
  *   - all counts are non-negative
  *   - total count (k + m + n) is at least 2
  *
  * Implemented as `sealed abstract case class` to prevent Scala 2.13's case-class
  * `apply` and `copy` synthesis from leaking around the smart constructor.
  */
sealed abstract case class Population(
    homozygousDominant: Int,
    heterozygous: Int,
    homozygousRecessive: Int
) {
  def total: Int = homozygousDominant + heterozygous + homozygousRecessive
}

object Population {
  private val MinTotal = 2

  def from(k: Int, m: Int, n: Int): Either[PopulationError, Population] =
    if (k < 0 || m < 0 || n < 0)
      Left(PopulationError.NegativeCount)
    else if (k + m + n < MinTotal)
      Left(PopulationError.InsufficientPopulation)
    else
      Right(new Population(k, m, n) {})
}
