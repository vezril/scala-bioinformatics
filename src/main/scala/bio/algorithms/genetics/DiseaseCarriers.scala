package bio.algorithms.genetics

import bio.domain.stats.Probability

/** Per-factor carrier probability under Hardy-Weinberg equilibrium (Rosalind AFRQ).
  *
  * For each `A[k] = q²` (the homozygous-recessive proportion) in the input, computes
  * `B[k] = 2q − A[k] = 2 · √A[k] − A[k]` — the probability that a randomly selected
  * diploid individual carries at least one copy of the recessive allele.
  *
  * **Derivation:** at Hardy-Weinberg equilibrium with recessive-allele frequency `q`,
  *   - `P(homozygous recessive) = q²` (= input `A[k]`)
  *   - `P(homozygous dominant) = p² = (1 − q)²`
  *   - `P(carrier) = 1 − P(both alleles dominant) = 1 − (1 − q)² = 2q − q²`
  *
  * Substituting `q = √A` gives `B = 2 · √A − A`.
  *
  * **Range safety:** the function `f(a) = 2√a − a` maps `[0, 1] → [0, 1]`:
  *   - `f(0) = 0`, `f(1) = 1`.
  *   - `f'(a) = 1/√a − 1 ≥ 0` for `a ∈ (0, 1]`, so `f` is monotonically non-decreasing.
  *
  * Every output is therefore a valid `Probability`, justifying `Probability.unsafeFrom`
  * (the framework's `private[bio]` trusted constructor — same pattern used by
  * [[MendelianInheritance.dominantProbability]] and [[IndependentAlleles]]).
  *
  * **Complexity:** O(n) per call. Each step is one `Math.sqrt`, one subtraction, one
  * multiplication.
  */
object DiseaseCarriers {

  def frequencies(homozygousRecessive: Vector[Probability]): Vector[Probability] =
    homozygousRecessive.map { p =>
      val a = p.value
      Probability.unsafeFrom(2.0 * Math.sqrt(a) - a)
    }
}
