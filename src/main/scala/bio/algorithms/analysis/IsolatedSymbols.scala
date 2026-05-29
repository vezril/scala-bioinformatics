package bio.algorithms.analysis

import bio.domain.analysis.{
  IsolatedSymbols => Result,
  IsolatedSymbolsProblem
}

/** Computes, for two DNA strings `s` and `t`, the maximum *global*
  * alignment score and the sum of the symbol-isolation matrix `M`
  * (Rosalind OSYM — "Isolating Symbols in Alignments").
  *
  * Scoring is the +1/-1 *mismatch score* from SIMS: `+1` per matched
  * symbol, `-1` per mismatched, inserted, or deleted symbol.
  *
  * `M[j][k]` is the maximum score of any global alignment of `s` and `t`
  * that places `s[j]` in the same column as `t[k]` (a forced
  * match/substitution column at `(j, k)`). Such an alignment decomposes
  * into three independent parts whose scores add:
  *
  *   - the best global alignment of the prefixes `s[0..j)` and `t[0..k)`;
  *   - the forced column scoring `sc(s[j], t[k])`;
  *   - the best global alignment of the suffixes `s[j+1..m)` and
  *     `t[k+1..n)`.
  *
  * **Algorithm — forward + backward `O(m · n)` DP.**
  *
  *   - forward `f(i)(j)` = best global score of `s[0..i)` vs `t[0..j)`:
  *     `f(0)(0) = 0`, `f(i)(0) = -i`, `f(0)(j) = -j`,
  *     `f(i)(j) = max(f(i-1)(j-1) + sc, f(i-1)(j) - 1, f(i)(j-1) - 1)`;
  *   - backward `b(i)(j)` = best global score of `s[i..m)` vs `t[j..n)`:
  *     `b(m)(n) = 0`, `b(i)(n) = -(m - i)`, `b(m)(j) = -(n - j)`,
  *     `b(i)(j) = max(b(i+1)(j+1) + sc, b(i+1)(j) - 1, b(i)(j+1) - 1)`.
  *
  * Then `globalScore = f(m)(n)` and, for `j ∈ [0, m)`, `k ∈ [0, n)`,
  * `M[j][k] = f(j)(k) + sc(s[j], t[k]) + b(j+1)(k+1)`. The matrix is never
  * materialised — its entries are summed directly into a `Long`
  * accumulator (the sum of up to `10^6` entries each near `-(m + n)` can
  * approach `-2 · 10^9` and overflow an `Int`).
  *
  * **Complexity.** `O(m · n)` time; `O(m · n)` memory (two int tables).
  * Both tables are required for the O(1)-per-cell `M` formula. At the
  * Rosalind cap `m, n ≤ 1000` that is ~8 MB and a few million operations.
  */
object IsolatedSymbols {

  /** Linear gap penalty (per inserted/deleted symbol), per the mismatch score. */
  private val Gap: Int = -1

  def compute(problem: IsolatedSymbolsProblem): Result = {
    val s = problem.left.value
    val t = problem.right.value
    val m = s.length
    val n = t.length

    // Forward DP: f(i)(j) = best global score of s[0..i) vs t[0..j).
    val f = Array.ofDim[Int](m + 1, n + 1)
    var i = 0
    while (i <= m) { f(i)(0) = -i; i += 1 }
    var j = 0
    while (j <= n) { f(0)(j) = -j; j += 1 }
    i = 1
    while (i <= m) {
      val si = s.charAt(i - 1)
      var jj = 1
      while (jj <= n) {
        val sc   = if (si == t.charAt(jj - 1)) 1 else -1
        val diag = f(i - 1)(jj - 1) + sc
        val up   = f(i - 1)(jj) + Gap
        val left = f(i)(jj - 1) + Gap
        f(i)(jj) = math.max(diag, math.max(up, left))
        jj += 1
      }
      i += 1
    }

    val globalScore = f(m)(n)

    // Backward DP: b(i)(j) = best global score of s[i..m) vs t[j..n).
    val b = Array.ofDim[Int](m + 1, n + 1)
    i = m
    while (i >= 0) { b(i)(n) = -(m - i); i -= 1 }
    j = n
    while (j >= 0) { b(m)(j) = -(n - j); j -= 1 }
    i = m - 1
    while (i >= 0) {
      val si = s.charAt(i)
      var jj = n - 1
      while (jj >= 0) {
        val sc   = if (si == t.charAt(jj)) 1 else -1
        val diag = b(i + 1)(jj + 1) + sc
        val down = b(i + 1)(jj) + Gap
        val right = b(i)(jj + 1) + Gap
        b(i)(jj) = math.max(diag, math.max(down, right))
        jj -= 1
      }
      i -= 1
    }

    // matrixSum = Σ M[j][k] over j ∈ [0, m), k ∈ [0, n), without
    // materialising M. Empty left or right ⇒ empty range ⇒ sum 0.
    var matrixSum = 0L
    i = 0
    while (i < m) {
      val si = s.charAt(i)
      var jj = 0
      while (jj < n) {
        val sc = if (si == t.charAt(jj)) 1 else -1
        matrixSum += (f(i)(jj) + sc + b(i + 1)(jj + 1)).toLong
        jj += 1
      }
      i += 1
    }

    Result(globalScore = globalScore, matrixSum = matrixSum)
  }
}
