package bio.domain.genetics

/** Validated input bundle for the Wright-Fisher genetic-drift algorithm
  * (Rosalind WFMD — see [[bio.algorithms.genetics.WrightFisher]]).
  *
  * Parameters:
  *   - `n`: number of diploid individuals (so the population has `2n` chromosomes).
  *   - `m`: initial count of dominant alleles in the `2n`-chromosome population.
  *   - `g`: number of Wright-Fisher generations to simulate.
  *   - `k`: tail threshold — we compute the probability of at least `k` recessive
  *     alleles in the final generation.
  *
  * Constructable only via [[WrightFisherProblem.from]] which enforces:
  *   - `1 <= n <= 7` (Rosalind upper bound)
  *   - `1 <= m <= 2 * n` (cross-constraint: can't have more dominant alleles than
  *     total alleles)
  *   - `1 <= g <= 6` (Rosalind upper bound)
  *   - `1 <= k <= 2 * n` (cross-constraint: can't ask for more recessive alleles than
  *     total alleles)
  *
  * Validation order: `n` lower → `n` upper → `m` lower → `m` upper → `g` lower →
  * `g` upper → `k` lower → `k` upper. First failure wins. `n` must be validated
  * first because `m` and `k`'s upper bounds depend on `2 * n`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class WrightFisherProblem(n: Int, m: Int, g: Int, k: Int)

object WrightFisherProblem {
  private val MaxN: Int = 7
  private val MaxG: Int = 6

  def from(
      n: Int,
      m: Int,
      g: Int,
      k: Int
  ): Either[WrightFisherProblemError, WrightFisherProblem] = {
    if (n < 1) Left(WrightFisherProblemError.NonPositiveN(n))
    else if (n > MaxN) Left(WrightFisherProblemError.NExceedsMaximum(n, MaxN))
    else if (m < 1) Left(WrightFisherProblemError.NonPositiveM(m))
    else if (m > 2 * n) Left(WrightFisherProblemError.MExceedsTotalAlleles(m, 2 * n))
    else if (g < 1) Left(WrightFisherProblemError.NonPositiveG(g))
    else if (g > MaxG) Left(WrightFisherProblemError.GExceedsMaximum(g, MaxG))
    else if (k < 1) Left(WrightFisherProblemError.NonPositiveK(k))
    else if (k > 2 * n) Left(WrightFisherProblemError.KExceedsTotalAlleles(k, 2 * n))
    else Right(new WrightFisherProblem(n, m, g, k) {})
  }
}
