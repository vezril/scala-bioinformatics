package bio.algorithms.analysis

import bio.domain.analysis.{RandomMotifMatch, RandomMotifProblem}
import bio.domain.nucleic.DnaNucleotide

/** Computes the probability that at least one of `N` random DNA strings equals a
  * given motif — Rosalind RSTR ("Matching Random Motifs").
  *
  * A random DNA string of GC-content `x` draws each symbol independently with
  * `P(G) = P(C) = x/2` and `P(A) = P(T) = (1 - x)/2`. The probability that one such
  * string of length `|s|` equals the motif `s` is the product `p` of its per-symbol
  * probabilities, and the probability that at least one of `N` independent strings
  * equals `s` is the complement of "none match": `1 - (1 - p)^N`.
  *
  * This is the linear-scale, single-GC-content companion to
  * [[bio.algorithms.analysis.RandomMatch]] (Rosalind PROB), which returns `log10`
  * match probabilities across a vector of GC-contents.
  *
  * Pure and total: per-symbol probabilities are dispatched on the `DnaNucleotide`
  * ADT (no raw `Char` literals) and there is no mutable state.
  */
object MatchingRandomMotifs {

  def probability(problem: RandomMotifProblem): RandomMotifMatch = {
    val x = problem.gcContent.value

    val p =
      problem.motif.value.iterator
        .flatMap(DnaNucleotide.fromChar)
        .map(symbolProbability(_, x))
        .product

    RandomMotifMatch(1.0 - math.pow(1.0 - p, problem.trials.toDouble))
  }

  /** The probability of drawing nucleotide `n` from a random string of GC-content `x`. */
  private def symbolProbability(n: DnaNucleotide, x: Double): Double = n match {
    case DnaNucleotide.G | DnaNucleotide.C => x / 2.0
    case DnaNucleotide.A | DnaNucleotide.T => (1.0 - x) / 2.0
  }
}
