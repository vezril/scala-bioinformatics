package bio.algorithms.analysis

import bio.domain.analysis.SharedSplicedMotifProblem

/** Finds a *longest common subsequence* (LCS) of two DNA strings (Rosalind
  * spec 39 — LCSQ, "Finding a Shared Spliced Motif").
  *
  * **Subsequence vs substring.** A *subsequence* is a sequence of symbols that
  * appears in the source *in order* — not necessarily contiguously. Contrast
  * with [[SharedMotif]] (LCSM, spec 38) which finds the longest common
  * *substring* (contiguous) across a *collection* of strings. The full
  * matrix in `bio.algorithms.analysis`:
  *
  *   - [[MotifLocations]] (SUBS, spec 9) — one-pattern *substring* search.
  *   - [[SplicedMotif]] (SSEQ, spec 37) — one-pattern *subsequence* search.
  *   - [[SharedMotif]] (LCSM, spec 38) — multi-string longest common
  *     *substring*.
  *   - `SharedSplicedMotif` (this) — two-string longest common *subsequence*.
  *
  * **Algorithm — classical `O(m · n)` LCS DP.** Build a `(m + 1) × (n + 1)`
  * `Array[Array[Int]]` table where `dp(i)(j)` is the LCS length of
  * `left[0..i)` and `right[0..j)`. Recurrence:
  *
  *   - `dp(0)(_) = dp(_)(0) = 0`;
  *   - if `left(i-1) == right(j-1)`: `dp(i)(j) = dp(i-1)(j-1) + 1`;
  *   - else: `dp(i)(j) = max(dp(i-1)(j), dp(i)(j-1))`.
  *
  * After filling, reconstruct one LCS by backtracking from `(m, n)`:
  *   - on a character match, prepend it and decrement both pointers;
  *   - on a mismatch, prefer "up" (`dp(i-1)(j) >= dp(i)(j-1)` ⇒ decrement
  *     `i`); else decrement `j`.
  *
  * The "prefer up on ties" rule is the textbook default and is the only place
  * the algorithm makes a deterministic choice among equivalent LCSes.
  *
  * **Multiple valid LCSes.** When several LCSes of the same maximum length
  * exist (the typical case), the spec permits any. Our deterministic
  * convention produces one specific answer that may differ from Rosalind's
  * published sample — both are correct per the grader. For the canonical
  * `(AACCTTGG, ACACTGTGA)` sample, Rosalind shows `"AACTGG"`; the convention
  * above produces a different length-6 LCS that is equally valid.
  *
  * **Complexity.** `O(m · n)` time and `O(m · n)` memory. At the Rosalind cap
  * `m, n ≤ 1000` that's 10⁶ cells — milliseconds and a few MB.
  */
object SharedSplicedMotif {

  def find(problem: SharedSplicedMotifProblem): String = {
    val s = problem.left.value
    val t = problem.right.value
    val m = s.length
    val n = t.length
    if (m == 0 || n == 0) ""
    else {
      val dp = Array.ofDim[Int](m + 1, n + 1)

      var i = 1
      while (i <= m) {
        var j = 1
        while (j <= n) {
          if (s.charAt(i - 1) == t.charAt(j - 1))
            dp(i)(j) = dp(i - 1)(j - 1) + 1
          else
            dp(i)(j) = math.max(dp(i - 1)(j), dp(i)(j - 1))
          j += 1
        }
        i += 1
      }

      // Backtrack from (m, n) to reconstruct one LCS.
      val sb = new StringBuilder
      var bi = m
      var bj = n
      while (bi > 0 && bj > 0) {
        if (s.charAt(bi - 1) == t.charAt(bj - 1)) {
          sb += s.charAt(bi - 1)
          bi -= 1
          bj -= 1
        } else if (dp(bi - 1)(bj) >= dp(bi)(bj - 1)) {
          bi -= 1
        } else {
          bj -= 1
        }
      }

      sb.reverse.toString
    }
  }
}
