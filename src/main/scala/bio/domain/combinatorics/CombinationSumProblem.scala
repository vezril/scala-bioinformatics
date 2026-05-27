package bio.domain.combinatorics

/** Validated input bundle for the modular tail-sum of binomial coefficients
  * (Rosalind ASPC) — see [[bio.algorithms.combinatorics.Combinations.sumFrom]].
  *
  * Constructable only via [[CombinationSumProblem.from]] which enforces:
  *   - `n >= 0` (Rosalind: `0 <= m <= n`)
  *   - `n <= 2000` (Rosalind upper bound)
  *   - `m >= 0`
  *   - `m <= n` (cross-constraint)
  *
  * Validation order: `n` lower bound, `n` upper bound, `m` lower bound, then the
  * `m <= n` cross-constraint. First failure wins.
  *
  * Note that the lower bound here is `0` (not `1` as in `PartialPermutationProblem`
  * or `SubsetUniverseSize`). Both `n = 0` and `m = 0` are valid inputs — the
  * resulting algorithm produces well-defined trivial outputs (`C(0, 0) = 1` and the
  * full row sum `2^n` respectively).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class CombinationSumProblem(n: Int, m: Int)

object CombinationSumProblem {
  private val MaxN: Int = 2000

  def from(
      n: Int,
      m: Int
  ): Either[CombinationSumProblemError, CombinationSumProblem] =
    if (n < 0) Left(CombinationSumProblemError.NegativeN(n))
    else if (n > MaxN) Left(CombinationSumProblemError.NExceedsMaximum(n, MaxN))
    else if (m < 0) Left(CombinationSumProblemError.NegativeM(m))
    else if (m > n) Left(CombinationSumProblemError.MExceedsN(m, n))
    else Right(new CombinationSumProblem(n, m) {})
}
