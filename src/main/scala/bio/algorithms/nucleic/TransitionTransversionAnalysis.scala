package bio.algorithms.nucleic

import bio.domain.nucleic.{TransitionTransversionProblem, TransitionTransversionRatio}

/** Computes the transition/transversion counts between two equal-length DNA
  * strings (Rosalind TRAN — "Transitions and Transversions").
  *
  * Over the mismatched positions (identified as for Hamming distance), a
  * substitution is a *transition* when both bases are purines (`A`/`G`) or both
  * are pyrimidines (`C`/`T`), and a *transversion* otherwise (a purine↔pyrimidine
  * swap). The result reports both counts, from which the ratio is derived.
  *
  * Single pass, `O(L)`. As in [[bio.algorithms.analysis.HammingDistance]], the
  * comparison is on the underlying `Char`s (both inputs are validated
  * `DnaString`s). The public [[analyze]] signature is pure and total.
  */
object TransitionTransversionAnalysis {

  private val Purines: Set[Char]     = Set('A', 'G')
  private val Pyrimidines: Set[Char] = Set('C', 'T')

  def analyze(problem: TransitionTransversionProblem): TransitionTransversionRatio = {
    val mismatches =
      problem.first.value.lazyZip(problem.second.value).filter { case (x, y) => x != y }
    val transitions   = mismatches.count { case (x, y) => isTransition(x, y) }
    val transversions = mismatches.size - transitions
    TransitionTransversionRatio(transitions, transversions)
  }

  /** True iff substituting `a` for `b` is a transition (purine↔purine or
    * pyrimidine↔pyrimidine).
    */
  private def isTransition(a: Char, b: Char): Boolean =
    (Purines(a) && Purines(b)) || (Pyrimidines(a) && Pyrimidines(b))
}
