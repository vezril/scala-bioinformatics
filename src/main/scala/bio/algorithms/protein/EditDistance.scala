package bio.algorithms.protein

import bio.domain.protein.EditDistanceProblem

/** Computes the *Levenshtein (edit) distance* between two protein strings
  * (Rosalind spec 40 — EDIT, "Edit Distance").
  *
  * The edit distance `d_E(s, t)` is the minimum number of single-symbol
  * substitutions, insertions, and deletions required to transform `s` into
  * `t`. It is symmetric in its two arguments and bounded by
  * `max(|s|, |t|)`.
  *
  * **Algorithm — classical `O(m · n)` Levenshtein DP.** Build a
  * `(m + 1) × (n + 1)` `Array[Array[Int]]` table where `dp(i)(j)` is the edit
  * distance between `left[0..i)` and `right[0..j)`. Recurrence:
  *
  *   - `dp(0)(j) = j` (transform empty prefix of `left` into `right[0..j)` by
  *     `j` insertions);
  *   - `dp(i)(0) = i` (transform `left[0..i)` into the empty prefix of `right`
  *     by `i` deletions);
  *   - if `left(i-1) == right(j-1)`: `dp(i)(j) = dp(i-1)(j-1)` (free match);
  *   - else: `dp(i)(j) = 1 + min(dp(i-1)(j), dp(i)(j-1), dp(i-1)(j-1))`,
  *     corresponding to a delete, insert, or substitute respectively.
  *
  * The answer is `dp(m)(n)`. When `left` is empty, the result is
  * `right.value.length`; when `right` is empty, the result is
  * `left.value.length`; when both are empty, the result is `0`. These cases
  * are folded into the recurrence naturally — no special-casing is needed.
  *
  * Contrast with [[bio.algorithms.analysis.SharedSplicedMotif]] (LCSQ, spec
  * 39) which uses the same table shape to compute the *longest common
  * subsequence* length. The two algorithms are duals: an LCS of length `L`
  * between strings of length `m` and `n` corresponds to an edit distance of
  * `m + n - 2L` when only insertions and deletions are allowed (no
  * substitutions). Here we allow substitutions too, so the answer can be
  * strictly smaller.
  *
  * **Complexity.** `O(m · n)` time and `O(m · n)` memory. At the Rosalind cap
  * `m, n ≤ 1000` that's 10⁶ cells — milliseconds and a few MB.
  */
object EditDistance {

  def compute(problem: EditDistanceProblem): Int = {
    val s = problem.left.value
    val t = problem.right.value
    val m = s.length
    val n = t.length

    val dp = Array.ofDim[Int](m + 1, n + 1)

    var i = 0
    while (i <= m) { dp(i)(0) = i; i += 1 }
    var j = 0
    while (j <= n) { dp(0)(j) = j; j += 1 }

    i = 1
    while (i <= m) {
      var jj = 1
      while (jj <= n) {
        if (s.charAt(i - 1) == t.charAt(jj - 1))
          dp(i)(jj) = dp(i - 1)(jj - 1)
        else {
          val del = dp(i - 1)(jj)     // delete s(i-1)
          val ins = dp(i)(jj - 1)     // insert t(j-1) into s
          val sub = dp(i - 1)(jj - 1) // substitute s(i-1) for t(j-1)
          dp(i)(jj) = 1 + math.min(del, math.min(ins, sub))
        }
        jj += 1
      }
      i += 1
    }

    dp(m)(n)
  }
}
