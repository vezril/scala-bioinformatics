package bio.algorithms.analysis

import bio.domain.analysis.{
  ExpectedRestrictionSitesProblem,
  ExpectedRestrictionSites => Result
}
import bio.domain.nucleic.DnaNucleotide

/** Computes the expected number of occurrences of a motif as a substring of a random
  * DNA string, one per GC-content — Rosalind EVAL ("Expected Number of Restriction
  * Sites").
  *
  * A motif of length `L` can begin at `n - L + 1` positions in a length-`n` string.
  * By linearity of expectation, the expected number of occurrences under GC-content
  * `x` is `max(0, n - L + 1) * p`, where `p = ∏ⱼ P(sⱼ | x)` is the probability the
  * motif matches at a single position (`P(G) = P(C) = x/2`, `P(A) = P(T) = (1 - x)/2`).
  *
  * This is the vector-of-GC-contents (like Rosalind PROB), linear-scale expected-count
  * companion to [[bio.algorithms.analysis.MatchingRandomMotifs]] (Rosalind RSTR),
  * which computes the single-GC-content "at least one of N" probability.
  *
  * Pure and total: per-symbol probabilities are dispatched on the `DnaNucleotide`
  * ADT (no raw `Char` literals), GC-contents map in input order, and there is no
  * mutable state.
  */
object ExpectedRestrictionSites {

  def expectedCounts(problem: ExpectedRestrictionSitesProblem): Result = {
    val positions = math.max(0, problem.length - problem.motif.value.length + 1).toDouble

    val expectations =
      problem.gcContents.map { gc =>
        positions * singlePositionProbability(problem.motif.value, gc.value)
      }

    Result(expectations)
  }

  /** The probability the motif matches at a single position under GC-content `x`. */
  private def singlePositionProbability(motif: String, x: Double): Double =
    motif.iterator
      .flatMap(DnaNucleotide.fromChar)
      .map(symbolProbability(_, x))
      .product

  /** The probability of drawing nucleotide `n` from a random string of GC-content `x`. */
  private def symbolProbability(n: DnaNucleotide, x: Double): Double = n match {
    case DnaNucleotide.G | DnaNucleotide.C => x / 2.0
    case DnaNucleotide.A | DnaNucleotide.T => (1.0 - x) / 2.0
  }
}
