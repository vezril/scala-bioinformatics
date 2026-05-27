package bio.algorithms.analysis

import bio.domain.analysis.RandomMatchProblem

/** Computes, for each input GC content `x`, the `log10` of the probability that a
  * random DNA string of length `|s|` constructed under symbol frequencies
  *
  *   P(G) = P(C) = x / 2
  *   P(A) = P(T) = (1 − x) / 2
  *
  * matches the given DNA string `s` exactly. (Rosalind PROB.)
  *
  * Algorithm: outer `.map` over `problem.gcContents`; for each GC value, inner
  * `foldLeft` over `problem.dna.value` summing `Math.log10` of the per-character
  * symbol probability.
  *
  * **Sum-of-logs (not log-of-product):** computing `log10(Π P(c))` directly would
  * underflow the product to `0.0` for long sequences. Summing logs sidesteps the
  * underflow regime entirely while producing identical results in the well-defined
  * range. For Rosalind bounds (`|s| ≤ 100`, `|gcContents| ≤ 20`) the product *would*
  * still be representable, but the log-sum form is the right idiom regardless — it's
  * the same FLOP count and avoids the footgun at scale.
  *
  * **Edge cases:** at `gc = 0`, `P(G) = P(C) = 0` so any sequence containing G or C
  * yields `Double.NegativeInfinity`. Symmetrically, `gc = 1` makes `P(A) = P(T) = 0`
  * and any sequence containing A or T yields `Double.NegativeInfinity`. This is
  * mathematically correct (the probability of the match is exactly 0) and is tested
  * as an explicit edge case.
  *
  * The inner per-character match exhaustively covers `{A, C, G, T}` — the only
  * possible characters in a validated `DnaString`. The `case _` arm is structurally
  * unreachable but kept to make the match total at the type-system level.
  */
object RandomMatch {

  def logProbabilities(problem: RandomMatchProblem): Vector[Double] = {
    val chars = problem.dna.value
    problem.gcContents.map { gc =>
      val x   = gc.value
      val pGc = x / 2.0
      val pAt = (1.0 - x) / 2.0
      chars.foldLeft(0.0) { (acc, ch) =>
        val p = ch match {
          case 'G' | 'C' => pGc
          case 'A' | 'T' => pAt
          case _         => pAt // unreachable: DnaString is alphabet-validated
        }
        acc + Math.log10(p)
      }
    }
  }
}
