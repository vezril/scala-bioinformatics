package bio.algorithms.analysis

import bio.domain.analysis.{InterwovenMotifMatrix, InterwovenMotifProblem}

/** Computes the Rosalind ITWV ("Finding Disjoint Motifs in a Gene") matrix.
  *
  * For a text string `s` and patterns `p_0 .. p_{n-1}`, produces the `n × n`
  * 0/1 matrix `M` with `M[j][k] = 1` iff patterns `j` and `k` can be
  * *interwoven* into `s`: some contiguous substring of `s` is an interleaving
  * (shuffle) of the two patterns, which appear as disjoint subsequences that
  * together cover that substring exactly.
  *
  * **Decision.** A pair `(t, u)` is interweavable iff, for some start `p`, the
  * window `s[p .. p+|t|+|u|-1]` is an interleaving of `t` and `u`. For one
  * window, fill the classic interleaving table `dp(i)(j)` = "the first `i+j`
  * window characters are an interleaving of `t[0..i-1]` and `u[0..j-1]`":
  *   - `dp(0)(0) = true`;
  *   - `dp(i)(j) = (i>0 && t(i-1)==s(p+i+j-1) && dp(i-1)(j)) ||`
  *                `(j>0 && u(j-1)==s(p+i+j-1) && dp(i)(j-1))`.
  * The pair is interweavable iff `dp(|t|)(|u|)` holds for any `p`. Cost per pair
  * `O(|s|·|t|·|u|)`; the relation is symmetric, so only the upper triangle is
  * computed and mirrored.
  *
  * The interleaving table is filled imperatively (`Array[Boolean]`, `while`),
  * confined to the private [[canInterweave]] helper, exactly as the alignment
  * family does; the public [[compute]] signature is pure and total.
  */
object InterwovenMotifs {

  def compute(problem: InterwovenMotifProblem): InterwovenMotifMatrix = {
    val s        = problem.text.value
    val patterns = problem.patterns.map(_.value)
    val n        = patterns.length

    val rows = Vector.tabulate(n, n) { (j, k) =>
      // Symmetric: compute the upper triangle, the lower mirrors it. Comparing
      // (j, k) only when j <= k avoids redundant DP; the tabulate below reads
      // the canonicalised pair so both M[j][k] and M[k][j] agree.
      val (a, b) = if (j <= k) (patterns(j), patterns(k)) else (patterns(k), patterns(j))
      if (canInterweave(s, a, b)) 1 else 0
    }

    InterwovenMotifMatrix(rows)
  }

  /** True iff some contiguous window of `s` is an interleaving of `t` and `u`. */
  private def canInterweave(s: String, t: String, u: String): Boolean = {
    val tLen = t.length
    val uLen = u.length
    val l    = tLen + uLen
    if (l > s.length) false
    else {
      var p     = 0
      var found = false
      while (!found && p <= s.length - l) {
        if (windowIsInterleaving(s, p, t, u)) found = true
        p += 1
      }
      found
    }
  }

  /** True iff `s[p .. p+|t|+|u|-1]` is an interleaving of `t` and `u`. */
  private def windowIsInterleaving(s: String, p: Int, t: String, u: String): Boolean = {
    val tLen = t.length
    val uLen = u.length
    val dp   = Array.ofDim[Boolean](tLen + 1, uLen + 1)
    dp(0)(0) = true

    var i = 0
    while (i <= tLen) {
      var j = 0
      while (j <= uLen) {
        if (i != 0 || j != 0) {
          val c = s.charAt(p + i + j - 1)
          val fromT = i > 0 && t.charAt(i - 1) == c && dp(i - 1)(j)
          val fromU = j > 0 && u.charAt(j - 1) == c && dp(i)(j - 1)
          dp(i)(j) = fromT || fromU
        }
        j += 1
      }
      i += 1
    }

    dp(tLen)(uLen)
  }
}
