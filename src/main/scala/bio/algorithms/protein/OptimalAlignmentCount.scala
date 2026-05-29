package bio.algorithms.protein

import bio.domain.protein.OptimalAlignmentCountProblem

/** Counts the number of distinct *optimal alignments* of two protein strings
  * under the standard Levenshtein metric (Rosalind spec 46 — CTEA,
  * "Counting Optimal Alignments"). The result is returned modulo
  * `134_217_727 = 2^27 - 1`.
  *
  * An alignment is *optimal* iff its Hamming distance over the augmented
  * strings equals the edit distance `d_E(s, t)` — i.e., the smallest number
  * of substitutions, insertions, and deletions needed to transform one
  * string into the other.
  *
  * **Algorithm — parallel Levenshtein cost + count DP.** Build two
  * `(m + 1) × (n + 1)` `Array[Array[Int]]` tables in lock-step:
  *
  *   - `dp(i)(j) = d_E(left[0..i), right[0..j))`;
  *   - `cnt(i)(j) = number of optimal alignments of left[0..i) and right[0..j)`.
  *
  * Boundary conditions:
  *   - `dp(0)(0) = 0`, `cnt(0)(0) = 1` (the empty alignment is the unique
  *     way to align two empty prefixes);
  *   - `dp(i)(0) = i`, `cnt(i)(0) = 1` (only deletions; one path);
  *   - `dp(0)(j) = j`, `cnt(0)(j) = 1` (only insertions; one path).
  *
  * Recurrence at interior cells (with `δ = 0` if `left(i-1) == right(j-1)`
  * else `1`):
  *
  *   - `diag = dp(i-1)(j-1) + δ`;
  *   - `up   = dp(i-1)(j) + 1`;
  *   - `left = dp(i)(j-1) + 1`;
  *   - `dp(i)(j) = min(diag, up, left)`;
  *   - `cnt(i)(j) = (Σ cnt(predecessor) over winning moves) mod 134_217_727`,
  *     where a move is "winning" iff its cost-extension equals `dp(i)(j)`.
  *
  * The answer is `cnt(m)(n)`.
  *
  * **Modulus note.** `134_217_727 = 2^27 - 1 = 7 × 73 × 262657` — NOT prime.
  * Counting via modular addition is still well-defined and the spec asks for
  * the residue, so non-primality is harmless.
  *
  * **Complexity.** `O(m · n)` time and `O(m · n)` memory across two tables.
  * At the Rosalind cap (`m, n ≤ 1000`) that's `~2 · 10^6` cells — milliseconds.
  */
object OptimalAlignmentCount {

  /** `134_217_727 = 2^27 - 1`. */
  private val Modulus: Int = 134_217_727

  def compute(problem: OptimalAlignmentCountProblem): Int = {
    val s = problem.left.value
    val t = problem.right.value
    val m = s.length
    val n = t.length

    val dp  = Array.ofDim[Int](m + 1, n + 1)
    val cnt = Array.ofDim[Int](m + 1, n + 1)

    var i = 0
    while (i <= m) { dp(i)(0) = i; cnt(i)(0) = 1; i += 1 }
    var j = 0
    while (j <= n) { dp(0)(j) = j; cnt(0)(j) = 1; j += 1 }

    i = 1
    while (i <= m) {
      var jj = 1
      while (jj <= n) {
        val matchFree = s.charAt(i - 1) == t.charAt(jj - 1)
        val diag      = dp(i - 1)(jj - 1) + (if (matchFree) 0 else 1)
        val up        = dp(i - 1)(jj) + 1
        val left      = dp(i)(jj - 1) + 1
        val best      = math.min(diag, math.min(up, left))
        dp(i)(jj) = best

        var c = 0
        if (diag == best) c = (c + cnt(i - 1)(jj - 1)) % Modulus
        if (up == best) c = (c + cnt(i - 1)(jj)) % Modulus
        if (left == best) c = (c + cnt(i)(jj - 1)) % Modulus
        cnt(i)(jj) = c

        jj += 1
      }
      i += 1
    }

    cnt(m)(n)
  }
}
