package bio.algorithms.genetics

import bio.domain.genetics.CouplePopulation

/** Computes the expected number of offspring displaying the dominant phenotype across a
  * population of couples, assuming each couple produces exactly two offspring.
  *
  * The per-couple expected count is the number of offspring (2) multiplied by the
  * probability that any given offspring expresses the dominant phenotype under standard
  * Mendelian assumptions:
  *
  *   - AA × AA: 1.0   → 2.0 expected dominant per couple
  *   - AA × Aa: 1.0   → 2.0
  *   - AA × aa: 1.0   → 2.0
  *   - Aa × Aa: 0.75  → 1.5
  *   - Aa × aa: 0.5   → 1.0
  *   - aa × aa: 0.0   → 0.0
  */
object ExpectedOffspring {

  def dominantPhenotype(pop: CouplePopulation): Double =
    2.0 * pop.homDomHomDom +
      2.0 * pop.homDomHet +
      2.0 * pop.homDomHomRec +
      1.5 * pop.hetHet +
      1.0 * pop.hetHomRec +
      0.0 * pop.homRecHomRec
}
