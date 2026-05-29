package bio.algorithms.analysis

import bio.domain.analysis.{
  FittingAlignment => Result,
  FittingAlignmentProblem
}

/** Computes one optimal *fitting alignment* of a motif `t` against the
  * best-matching substring of a text `s` under the *mismatch score* (`+1`
  * per matched symbol, `-1` per mismatched, inserted, or deleted symbol)
  * — Rosalind SIMS ("Finding a Motif with Modifications").
  *
  * A *fitting alignment* aligns **all** of `t` against a *substring* `r ⊆ s`.
  * It is the "semi-global" member of the alignment taxonomy: global with
  * respect to the motif, local with respect to the text. Contrast:
  *
  *   - **Global** (GLOB/EDTA): all of `s` against all of `t`.
  *   - **Local** (LOCA, Smith-Waterman): a substring of `s` against a
  *     substring of `t`, with a 0-clamp at every cell.
  *   - **Fitting** (this): a substring of `s` against *all* of `t`.
  *
  * **Algorithm — classical `O(m · n)` DP + traceback.** Build `dp(i)(j)`
  * over `i ∈ [0, m]` (text `s`) and `j ∈ [0, n]` (motif `t`):
  *
  *   - `dp(i)(0) = 0` for every `i` — starting the `s`-substring anywhere
  *     is free (local freedom on the text side; no 0-clamp, no full-gap
  *     top row);
  *   - `dp(0)(j) = -j` — the empty `s`-substring pays one gap per motif
  *     symbol (global constraint on the motif side);
  *   - `dp(i)(j) = max(dp(i-1)(j-1) + (if s(i-1) == t(j-1) then +1 else -1),
  *     dp(i-1)(j) - 1, dp(i)(j-1) - 1)`.
  *
  * The optimum is the maximum over the final column,
  * `maxScore = max over i of dp(i)(n)`, taken at the **smallest** such `i`
  * (a larger `i` with the same score would have arrived via a `-1` up-move,
  * implying a strictly-greater cell — so the smallest `i` has no trailing
  * unmatched text characters).
  *
  * Traceback from `(maxI, n)`, tie-break **diagonal > up > left**, stops
  * when the motif index reaches `0` (the skipped prefix of `s` is free and
  * not emitted). On the border `i == 0` the move is forced left. Emits per
  * move into reversed builders:
  *
  *   - **diagonal**: `s(i-1)` to the text builder, `t(j-1)` to the motif
  *     builder; decrement both.
  *   - **up**: `s(i-1)` to the text builder, `-` to the motif builder;
  *     decrement `i`.
  *   - **left**: `-` to the text builder, `t(j-1)` to the motif builder;
  *     decrement `j`.
  *
  * Both builders are reversed once at the end. The augmented strings are
  * plain `String` (they contain `-`, not a valid DNA base).
  *
  * **Complexity.** `O(m · n)` time and memory. At the SIMS caps
  * `m ≤ 10000`, `n ≤ 1000` that is `~10^7` cells (~40 MB) — traceback
  * needs the full table, so a row-rolling optimisation is not applicable.
  */
object FittingAlignment {

  /** Linear gap penalty (per inserted/deleted symbol), per SIMS mismatch score. */
  private val Gap: Int = -1

  def align(problem: FittingAlignmentProblem): Result = {
    val s = problem.text.value
    val t = problem.motif.value
    val m = s.length
    val n = t.length

    // Empty motif: the empty motif fits the empty substring at score 0.
    if (n == 0) return Result(0, "", "")

    val dp = Array.ofDim[Int](m + 1, n + 1)
    // dp(i)(0) = 0 for all i (Array.ofDim default) — free start anywhere.
    // dp(0)(j) = -j — empty text pays one gap per motif symbol.
    var j0 = 1
    while (j0 <= n) {
      dp(0)(j0) = dp(0)(j0 - 1) + Gap
      j0 += 1
    }

    var i = 1
    while (i <= m) {
      val si = s.charAt(i - 1)
      var jj = 1
      while (jj <= n) {
        val matchScore = if (si == t.charAt(jj - 1)) 1 else -1
        val diag       = dp(i - 1)(jj - 1) + matchScore
        val up         = dp(i - 1)(jj) + Gap
        val left       = dp(i)(jj - 1) + Gap
        dp(i)(jj) = math.max(diag, math.max(up, left))
        jj += 1
      }
      i += 1
    }

    // Answer = max over the final column, at the smallest attaining i.
    var maxScore = dp(0)(n)
    var maxI     = 0
    i = 1
    while (i <= m) {
      if (dp(i)(n) > maxScore) {
        maxScore = dp(i)(n)
        maxI = i
      }
      i += 1
    }

    // Traceback from (maxI, n) until the motif is fully consumed (j == 0).
    // Tie-break: diagonal > up > left. On the border i == 0, force left.
    val textSb  = new StringBuilder
    val motifSb = new StringBuilder
    var bi      = maxI
    var bj      = n
    while (bj > 0) {
      val cur = dp(bi)(bj)
      if (bi == 0) {
        // Forced left: empty text remaining, consume motif as a gap.
        textSb += '-'
        motifSb += t.charAt(bj - 1)
        bj -= 1
      } else {
        val matchScore = if (s.charAt(bi - 1) == t.charAt(bj - 1)) 1 else -1
        val diag       = dp(bi - 1)(bj - 1) + matchScore
        val up         = dp(bi - 1)(bj) + Gap
        if (cur == diag) {
          textSb += s.charAt(bi - 1)
          motifSb += t.charAt(bj - 1)
          bi -= 1
          bj -= 1
        } else if (cur == up) {
          textSb += s.charAt(bi - 1)
          motifSb += '-'
          bi -= 1
        } else {
          // Forced left: cur == dp(bi)(bj-1) + Gap.
          textSb += '-'
          motifSb += t.charAt(bj - 1)
          bj -= 1
        }
      }
    }

    Result(
      score = maxScore,
      augmentedText = textSb.reverse.toString,
      augmentedMotif = motifSb.reverse.toString
    )
  }
}
