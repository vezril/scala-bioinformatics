package bio.domain.genetics

import bio.domain.stats.Probability

/** Validated input bundle for the Wright-Fisher expectation algorithm
  * (Rosalind EBIN — see [[bio.algorithms.genetics.WrightFisher.expectedFrequencies]]).
  *
  * Parameters:
  *   - `n`: number of trials per binomial random variable (the Rosalind problem
  *     interprets this as the next-generation population's allele count).
  *   - `p`: a vector of allele-frequency probabilities. Each element `p(k)` is the
  *     success probability of the `k`-th `Bin(n, p(k))` distribution.
  *
  * Constructable only via [[WrightFisherExpectationProblem.from]] which enforces:
  *   - `1 <= n <= 1_000_000` (Rosalind upper bound)
  *   - `p.size <= 20` (Rosalind upper bound)
  *
  * Validation order: `n` lower → `n` upper → `p.size` upper. First failure wins.
  *
  * An empty `p` vector is **accepted** — it produces an empty output. Rosalind's
  * "array of length m" framing doesn't preclude `m = 0`.
  *
  * **Distinct from `WrightFisherProblem`:** that bundle is for the `WrightFisher.atLeast`
  * tail-probability algorithm and carries four parameters (`n`, `m`, `g`, `k`); this
  * bundle is for `WrightFisher.expectedFrequencies` and carries two (`n`, `p`). Each
  * method takes the *minimal* validated input it needs.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class WrightFisherExpectationProblem(
    n: Int,
    p: Vector[Probability]
)

object WrightFisherExpectationProblem {
  private val MaxN: Int          = 1_000_000
  private val MaxProbabilities: Int = 20

  def from(
      n: Int,
      p: Vector[Probability]
  ): Either[WrightFisherExpectationProblemError, WrightFisherExpectationProblem] =
    if (n < 1) Left(WrightFisherExpectationProblemError.NonPositiveN(n))
    else if (n > MaxN) Left(WrightFisherExpectationProblemError.NExceedsMaximum(n, MaxN))
    else if (p.size > MaxProbabilities)
      Left(WrightFisherExpectationProblemError.TooManyProbabilities(p.size, MaxProbabilities))
    else Right(new WrightFisherExpectationProblem(n, p) {})
}
