package bio.algorithms.protein

import bio.domain.protein.{EditAlignment, EditDistanceAlignmentProblem}

/** Computes one *optimal alignment* of two protein strings under the
  * Levenshtein (edit) distance (Rosalind spec 41 — EDTA, "Edit Distance
  * Alignment").
  *
  * An *alignment* is a pair of *augmented strings* `s'` and `t'` of equal
  * length, formed by inserting `-` gap symbols into the originals such that
  * no column has gaps in both rows, and such that the Hamming distance
  * `d_H(s', t')` equals the edit distance `d_E(s, t)`. An alignment is
  * *optimal* iff its Hamming distance attains the minimum (the edit
  * distance).
  *
  * **Algorithm — classical `O(m · n)` Levenshtein DP + greedy traceback.**
  * Build a `(m + 1) × (n + 1)` `Array[Array[Int]]` table where `dp(i)(j)` is
  * the edit distance between `left[0..i)` and `right[0..j)`, using the
  * standard recurrence:
  *
  *   - `dp(0)(j) = j`, `dp(i)(0) = i`;
  *   - if `s(i-1) == t(j-1)`: `dp(i)(j) = dp(i-1)(j-1)`;
  *   - else: `dp(i)(j) = 1 + min(dp(i-1)(j), dp(i)(j-1), dp(i-1)(j-1))`.
  *
  * Then traceback from `(m, n)` to `(0, 0)`, emitting at each step the
  * symbols going into the *reversed* augmented strings, and choosing the
  * incoming move with the tie-break order:
  *
  *   1. **diagonal-match** — `s(i-1) == t(j-1)` and `dp(i)(j) == dp(i-1)(j-1)`;
  *   2. **up (delete)** — `dp(i)(j) == dp(i-1)(j) + 1`;
  *   3. **left (insert)** — `dp(i)(j) == dp(i)(j-1) + 1`;
  *   4. **diagonal-sub (forced)** — otherwise.
  *
  * Preferring indels over substitution on cost-ties pushes gaps toward
  * trailing positions and reproduces the published Rosalind sample alignment
  * `PRETTY--` / `PR-TTEIN` for the canonical input `(PRETTY, PRTTEIN)`. On
  * the borders (`i == 0` ⇒ only-left; `j == 0` ⇒ only-up) the move is forced.
  *
  * **Why not reuse [[EditDistance.compute]]?** Traceback needs the full DP
  * table, not just the final `dp(m)(n)`. Reusing `compute` would force a
  * second pass to rebuild the table. The recurrence is ~10 lines; both
  * algorithms keep their own self-contained DP loop.
  *
  * **Output type.** The two augmented strings are plain `String` (not
  * `ProteinString`) because they may contain the `-` gap symbol, which is
  * not a valid amino-acid code.
  *
  * **Complexity.** `O(m · n)` time and `O(m · n)` memory. At the Rosalind
  * cap `m, n ≤ 1000` that's 10⁶ cells — milliseconds and a few MB.
  */
object EditDistanceAlignment {

  def align(problem: EditDistanceAlignmentProblem): EditAlignment = {
    val s = problem.left.value
    val t = problem.right.value
    val m = s.length
    val n = t.length

    // Build the DP table.
    val dp = Array.ofDim[Int](m + 1, n + 1)
    var i  = 0
    while (i <= m) { dp(i)(0) = i; i += 1 }
    var j  = 0
    while (j <= n) { dp(0)(j) = j; j += 1 }
    i = 1
    while (i <= m) {
      var jj = 1
      while (jj <= n) {
        if (s.charAt(i - 1) == t.charAt(jj - 1))
          dp(i)(jj) = dp(i - 1)(jj - 1)
        else {
          val del = dp(i - 1)(jj)
          val ins = dp(i)(jj - 1)
          val sub = dp(i - 1)(jj - 1)
          dp(i)(jj) = 1 + math.min(del, math.min(ins, sub))
        }
        jj += 1
      }
      i += 1
    }

    // Traceback. Build augmented strings reversed via StringBuilder, then
    // reverse once at the end (avoids O(n²) string concatenation).
    val leftSb  = new StringBuilder
    val rightSb = new StringBuilder
    var bi      = m
    var bj      = n
    while (bi > 0 || bj > 0) {
      if (bi == 0) {
        // Forced left: empty left, insert remaining right.
        leftSb += '-'
        rightSb += t.charAt(bj - 1)
        bj -= 1
      } else if (bj == 0) {
        // Forced up: empty right, delete remaining left.
        leftSb += s.charAt(bi - 1)
        rightSb += '-'
        bi -= 1
      } else if (s.charAt(bi - 1) == t.charAt(bj - 1) && dp(bi)(bj) == dp(bi - 1)(bj - 1)) {
        // Diagonal-match (free).
        leftSb += s.charAt(bi - 1)
        rightSb += t.charAt(bj - 1)
        bi -= 1
        bj -= 1
      } else if (dp(bi)(bj) == dp(bi - 1)(bj) + 1) {
        // Up: delete from left.
        leftSb += s.charAt(bi - 1)
        rightSb += '-'
        bi -= 1
      } else if (dp(bi)(bj) == dp(bi)(bj - 1) + 1) {
        // Left: insert into left.
        leftSb += '-'
        rightSb += t.charAt(bj - 1)
        bj -= 1
      } else {
        // Diagonal-sub (forced).
        leftSb += s.charAt(bi - 1)
        rightSb += t.charAt(bj - 1)
        bi -= 1
        bj -= 1
      }
    }

    EditAlignment(
      distance = dp(m)(n),
      augmentedLeft = leftSb.reverse.toString,
      augmentedRight = rightSb.reverse.toString
    )
  }
}
