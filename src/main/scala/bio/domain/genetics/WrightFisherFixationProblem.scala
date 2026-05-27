package bio.domain.genetics

/** Validated input bundle for the Wright-Fisher fixation algorithm
  * (Rosalind FOUN — see [[bio.algorithms.genetics.WrightFisher.fixationLogProbs]]).
  *
  * Parameters:
  *   - `n`: number of diploid individuals (so the population has `2n` chromosomes).
  *   - `m`: number of Wright-Fisher generations to simulate.
  *   - `recessiveCounts`: per-factor counts of recessive alleles (in `[0, 2n]`). Each
  *     element `recessiveCounts(j)` is the starting recessive-allele count for factor
  *     `j`; the corresponding dominant count is `2n − recessiveCounts(j)`.
  *
  * Constructable only via [[WrightFisherFixationProblem.from]] which enforces:
  *   - `1 <= n <= 100`
  *   - `1 <= m <= 100`
  *   - `recessiveCounts.size <= 100`
  *   - `0 <= recessiveCounts(j) <= 2 * n` for every element
  *
  * Validation order: `n` lower → `n` upper → `m` lower → `m` upper → `|A|` upper →
  * first out-of-range element in `recessiveCounts`. First failure wins. `n` is
  * validated before the per-element check because the upper bound `2n` depends on it.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class WrightFisherFixationProblem(
    n: Int,
    m: Int,
    recessiveCounts: Vector[Int]
)

object WrightFisherFixationProblem {
  private val MaxN: Int = 100
  private val MaxM: Int = 100
  private val MaxK: Int = 100

  def from(
      n: Int,
      m: Int,
      recessiveCounts: Vector[Int]
  ): Either[WrightFisherFixationProblemError, WrightFisherFixationProblem] = {
    if (n < 1) Left(WrightFisherFixationProblemError.NonPositiveN(n))
    else if (n > MaxN) Left(WrightFisherFixationProblemError.NExceedsMaximum(n, MaxN))
    else if (m < 1) Left(WrightFisherFixationProblemError.NonPositiveM(m))
    else if (m > MaxM) Left(WrightFisherFixationProblemError.MExceedsMaximum(m, MaxM))
    else if (recessiveCounts.size > MaxK)
      Left(WrightFisherFixationProblemError.TooManyRecessiveCounts(recessiveCounts.size, MaxK))
    else {
      val twoN = 2 * n
      recessiveCounts.zipWithIndex.find { case (a, _) => a < 0 || a > twoN } match {
        case Some((a, idx)) =>
          Left(WrightFisherFixationProblemError.RecessiveCountOutOfRange(idx, a, twoN))
        case None =>
          Right(new WrightFisherFixationProblem(n, m, recessiveCounts) {})
      }
    }
  }
}
