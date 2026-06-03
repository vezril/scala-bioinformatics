package bio.algorithms.analysis

import bio.domain.analysis.{MaxGapProblem, MaxGapSymbols}

/** Computes the maximum number of gap symbols in any maximum-score alignment of two DNA
  * strings — Rosalind MGAP ("Maximizing the Gap Symbols of an Optimal Alignment").
  *
  * For scoring with `m > 0`, `d < 0`, `g < 0` the result is independent of the exact
  * parameters: a maximum-score alignment matches a longest common subsequence, and the
  * gap-maximising optimal alignment opposes every other symbol with a gap. The matched
  * `LCS` columns consume one symbol from each string, leaving `(|s| − LCS) + (|t| − LCS)`
  * symbols each opposite a gap, so the maximum gap count is `|s| + |t| − 2·LCS(s,t)`.
  *
  * The longest-common-subsequence length is found by a rolling-row dynamic program
  * (the alignment-family imperative-DP exception: `var`/`while`/`Array` internally),
  * using O(min(|s|,|t|)) space. The public `maxGaps` is pure and total.
  */
object MaximizeGapSymbols {

  def maxGaps(problem: MaxGapProblem): MaxGapSymbols = {
    val s = problem.s.value
    val t = problem.t.value
    MaxGapSymbols(s.length + t.length - 2 * lcsLength(s, t))
  }

  /** Length of the longest common subsequence of `a` and `b`, via a two-row DP. */
  private def lcsLength(a: String, b: String): Int = {
    // Iterate over the longer string's rows; keep the shorter string as the inner
    // dimension so the two rolling rows have length min(|a|,|b|) + 1.
    val (outer, inner) = if (a.length >= b.length) (a, b) else (b, a)
    val width          = inner.length

    var prev = Array.fill(width + 1)(0)
    var curr = Array.fill(width + 1)(0)

    var i = 1
    while (i <= outer.length) {
      var j = 1
      while (j <= width) {
        curr(j) =
          if (outer.charAt(i - 1) == inner.charAt(j - 1)) prev(j - 1) + 1
          else math.max(prev(j), curr(j - 1))
        j += 1
      }
      val tmp = prev
      prev = curr
      curr = tmp
      i += 1
    }

    prev(width)
  }
}
