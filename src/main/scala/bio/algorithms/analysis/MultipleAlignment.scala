package bio.algorithms.analysis

import bio.domain.analysis.{MultipleAlignment => Alignment, MultipleAlignmentProblem}

/** Computes one *optimal multiple alignment* of four DNA strings under the
  * linear scoring scheme `match = 0`, `mismatch = -1`, summed over all
  * `C(4, 2) = 6` augmented-string pairs (Rosalind spec 43 — MULT,
  * "Multiple Alignment").
  *
  * Per-column scoring: for every unordered pair of rows `(j, k)` with
  * `j < k`, the pair contributes:
  *
  *   - `0` if both rows match in that column (including the gap-vs-gap case);
  *   - `-1` otherwise (chars differ, or one is gap and the other is not).
  *
  * **Algorithm — 4-dimensional `O((n_0 + 1)(n_1 + 1)(n_2 + 1)(n_3 + 1) · 15)`
  * DP + greedy traceback.** Build a flat `Array[Int]` DP table indexed by
  * `idx(i_0, i_1, i_2, i_3)`. For every cell except `(0, 0, 0, 0)`, iterate
  * over the 15 non-empty subsets `mask ∈ 1..15` of `{0, 1, 2, 3}` (bitmask
  * representation). A subset is legal if for every `k` with bit `k` set,
  * `i_k > 0`. The transition score is `dp[predecessor] + columnScore(mask)`
  * where `predecessor` decrements every `i_k` whose bit is set, and
  * `columnScore` is the sum over the 6 pairs as defined above. Take the
  * maximum over legal moves and record the chosen mask in a parallel
  * `Array[Byte]` for traceback.
  *
  * After filling, traceback from `(n_0, n_1, n_2, n_3)` to `(0, 0, 0, 0)`,
  * building each row's augmented string in reverse via `StringBuilder`s,
  * then reverse once at the end.
  *
  * **Tie-break.** When multiple masks achieve the same maximum, the
  * implementation takes the *first* one encountered in ascending iteration
  * order `mask = 1..15` (via strict `>` comparison). This is deterministic
  * but is not guaranteed to match Rosalind's published sample alignment —
  * the spec accepts any optimal alignment, so determinism is purely a
  * quality-of-life choice for tests and diffs.
  *
  * **Output type.** The four augmented strings are plain `String` (not
  * `DnaString`) because they may contain the `-` gap symbol, which is not a
  * valid DNA character.
  *
  * **Complexity.** At the Rosalind cap `n ≤ 10` per string the table holds
  * `11^4 = 14_641` cells and each cell tries 15 masks — `~220k` cell
  * transitions × 6 pair-comparisons. Trivially fast.
  */
object MultipleAlignment {

  private val NumRows: Int    = 4
  private val NumMasks: Int   = 1 << NumRows // 16; we iterate masks 1..15
  private val NumPairs: Int   = NumRows * (NumRows - 1) / 2 // 6 (unused; documentary)
  private val Unreachable: Int = Int.MinValue / 4 // guard against accidental promotion

  def align(problem: MultipleAlignmentProblem): Alignment = {
    val rows = problem.strings.map(_.value).toArray
    val n    = Array(rows(0).length, rows(1).length, rows(2).length, rows(3).length)

    // Flat-array dimensions: size = (n0+1)(n1+1)(n2+1)(n3+1).
    val d0 = n(0) + 1
    val d1 = n(1) + 1
    val d2 = n(2) + 1
    val d3 = n(3) + 1
    val total = d0 * d1 * d2 * d3

    val dp     = Array.fill[Int](total)(Unreachable)
    val chosen = Array.fill[Byte](total)(0)
    dp(idx(0, 0, 0, 0, d1, d2, d3)) = 0

    var i0 = 0
    while (i0 <= n(0)) {
      var i1 = 0
      while (i1 <= n(1)) {
        var i2 = 0
        while (i2 <= n(2)) {
          var i3 = 0
          while (i3 <= n(3)) {
            if (!(i0 == 0 && i1 == 0 && i2 == 0 && i3 == 0)) {
              var best     = Unreachable
              var bestMask = 0
              var mask     = 1
              while (mask < NumMasks) {
                if (legal(mask, i0, i1, i2, i3)) {
                  val pi0 = if ((mask & 1) != 0) i0 - 1 else i0
                  val pi1 = if ((mask & 2) != 0) i1 - 1 else i1
                  val pi2 = if ((mask & 4) != 0) i2 - 1 else i2
                  val pi3 = if ((mask & 8) != 0) i3 - 1 else i3
                  val pred = dp(idx(pi0, pi1, pi2, pi3, d1, d2, d3))
                  if (pred != Unreachable) {
                    val cand = pred + columnScore(mask, rows, i0, i1, i2, i3)
                    if (cand > best) {
                      best = cand
                      bestMask = mask
                    }
                  }
                }
                mask += 1
              }
              dp(idx(i0, i1, i2, i3, d1, d2, d3)) = best
              chosen(idx(i0, i1, i2, i3, d1, d2, d3)) = bestMask.toByte
            }
            i3 += 1
          }
          i2 += 1
        }
        i1 += 1
      }
      i0 += 1
    }

    // Traceback. Build augmented rows in reverse, then reverse once.
    val sbs = Array.fill(NumRows)(new StringBuilder)
    var bi0 = n(0)
    var bi1 = n(1)
    var bi2 = n(2)
    var bi3 = n(3)
    while (bi0 > 0 || bi1 > 0 || bi2 > 0 || bi3 > 0) {
      val mask = chosen(idx(bi0, bi1, bi2, bi3, d1, d2, d3)).toInt & 0xff
      // Emit the column described by `mask` into each row builder.
      sbs(0) += (if ((mask & 1) != 0) rows(0).charAt(bi0 - 1) else '-')
      sbs(1) += (if ((mask & 2) != 0) rows(1).charAt(bi1 - 1) else '-')
      sbs(2) += (if ((mask & 4) != 0) rows(2).charAt(bi2 - 1) else '-')
      sbs(3) += (if ((mask & 8) != 0) rows(3).charAt(bi3 - 1) else '-')
      // Step to predecessor.
      if ((mask & 1) != 0) bi0 -= 1
      if ((mask & 2) != 0) bi1 -= 1
      if ((mask & 4) != 0) bi2 -= 1
      if ((mask & 8) != 0) bi3 -= 1
    }

    Alignment(
      score = dp(idx(n(0), n(1), n(2), n(3), d1, d2, d3)),
      augmentedStrings = Vector(
        sbs(0).reverse.toString,
        sbs(1).reverse.toString,
        sbs(2).reverse.toString,
        sbs(3).reverse.toString
      )
    )
  }

  /** Flat-array index for the 4-D cell `(i0, i1, i2, i3)`. */
  private def idx(i0: Int, i1: Int, i2: Int, i3: Int, d1: Int, d2: Int, d3: Int): Int =
    ((i0 * d1 + i1) * d2 + i2) * d3 + i3

  /** A move is legal iff every row whose bit is set in `mask` has an
    * unconsumed character available.
    */
  private def legal(mask: Int, i0: Int, i1: Int, i2: Int, i3: Int): Boolean = {
    if ((mask & 1) != 0 && i0 == 0) return false
    if ((mask & 2) != 0 && i1 == 0) return false
    if ((mask & 4) != 0 && i2 == 0) return false
    if ((mask & 8) != 0 && i3 == 0) return false
    true
  }

  /** Sum of `-1` per disagreeing pair in the column described by `mask`
    * (bit `k` set ⇒ row `k` emits `rows(k).charAt(i_k - 1)`; bit `k` clear ⇒
    * row `k` emits gap `-`). Disagreement counts char-vs-gap and
    * char-vs-different-char; char-vs-same-char and gap-vs-gap both score
    * `0`.
    */
  private def columnScore(
      mask: Int,
      rows: Array[String],
      i0: Int, i1: Int, i2: Int, i3: Int
  ): Int = {
    val c0 = if ((mask & 1) != 0) rows(0).charAt(i0 - 1) else '-'
    val c1 = if ((mask & 2) != 0) rows(1).charAt(i1 - 1) else '-'
    val c2 = if ((mask & 4) != 0) rows(2).charAt(i2 - 1) else '-'
    val c3 = if ((mask & 8) != 0) rows(3).charAt(i3 - 1) else '-'
    var s = 0
    if (c0 != c1) s -= 1
    if (c0 != c2) s -= 1
    if (c0 != c3) s -= 1
    if (c1 != c2) s -= 1
    if (c1 != c3) s -= 1
    if (c2 != c3) s -= 1
    s
  }

  // Silence unused-warning if NumPairs is removed later; here as documentation.
  locally(NumPairs)
}
