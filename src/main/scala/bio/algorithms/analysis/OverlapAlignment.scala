package bio.algorithms.analysis

import bio.domain.analysis.{
  OverlapAlignment => Result,
  OverlapAlignmentProblem
}

/** Computes one optimal *overlap alignment* of a suffix of `s` against a prefix
  * of `t` under the score (`+1` match, `-2` substitution, `-2` gap) — Rosalind
  * OAP ("Overlap Alignment").
  *
  * An *overlap alignment* is the "semi-global" member of the alignment taxonomy
  * that pairs the **end of one string with the start of another** — exactly what
  * read-overlap detection in assembly needs. Contrast:
  *
  *   - **Global** (GLOB/EDTA): all of `s` against all of `t`.
  *   - **Local** (LOCA): a substring of `s` against a substring of `t`.
  *   - **Fitting** (SIMS): a substring of `s` against *all* of `t`.
  *   - **Overlap** (this): a *suffix* of `s` against a *prefix* of `t`.
  *
  * **Algorithm — classical `O(m · n)` DP + traceback.** Build `dp(i)(j)` over
  * `i ∈ [0, m]` (rows = `s`) and `j ∈ [0, n]` (cols = `t`):
  *
  *   - `dp(i)(0) = 0` for every `i` — the aligned suffix of `s` may start
  *     anywhere; the skipped prefix of `s` is free.
  *   - `dp(0)(j) = -2·j` — with an empty `s` portion, consuming `j` symbols of
  *     `t`'s prefix costs one gap each (the prefix of `t` is *consumed*, not
  *     skipped).
  *   - `dp(i)(j) = max(dp(i-1)(j-1) + (if s(i-1) == t(j-1) then +1 else -2),
  *     dp(i-1)(j) - 2, dp(i)(j-1) - 2)`.
  *
  * The optimum is the maximum over the **final row**,
  * `maxScore = max over j of dp(m)(j)`, taken at the **smallest** such `j` (a
  * larger `j` with the same score would append trailing `t`-prefix symbols as
  * `-2` gaps, so the smallest `j` has no trailing gap-only columns). Because
  * `dp(m)(0) = 0` is always a candidate, the score is never negative — the empty
  * overlap is always available.
  *
  * Traceback from `(m, maxJ)`, tie-break **diagonal > up > left**, stops when the
  * column index reaches `0` (the skipped prefix of `s` is free and not emitted).
  * On the border `i == 0` the move is forced left. Emits per move into reversed
  * builders:
  *
  *   - **diagonal**: `s(i-1)` to the `s` builder, `t(j-1)` to the `t` builder;
  *     decrement both.
  *   - **up**: `s(i-1)` to the `s` builder, `-` to the `t` builder; decrement `i`.
  *   - **left**: `-` to the `s` builder, `t(j-1)` to the `t` builder; decrement `j`.
  *
  * Both builders are reversed once at the end. The augmented strings are plain
  * `String` (they contain `-`, not a valid DNA base).
  *
  * **Complexity.** `O(m · n)` time and memory. Traceback needs the full table, so
  * a row-rolling optimisation is not applicable. The imperative kernel
  * (`var`/`while`/`Array`) is the established convention for this project's
  * alignment family; the public signature stays pure and total.
  */
object OverlapAlignment {

  /** Linear gap penalty (per inserted/deleted symbol), per OAP score. */
  private val Gap: Int = -2

  /** Substitution penalty (per mismatched symbol), per OAP score. */
  private val Mismatch: Int = -2

  def align(problem: OverlapAlignmentProblem): Result = {
    val s = problem.s.value
    val t = problem.t.value
    val m = s.length
    val n = t.length

    // Empty t: the only prefix of t is empty, so the optimal overlap is empty.
    if (n == 0) return Result(0, "", "")

    val dp = Array.ofDim[Int](m + 1, n + 1)
    // dp(i)(0) = 0 for all i (Array.ofDim default) — free start anywhere in s.
    // dp(0)(j) = -2·j — empty s pays one gap per consumed t-prefix symbol.
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
        val matchScore = if (si == t.charAt(jj - 1)) 1 else Mismatch
        val diag       = dp(i - 1)(jj - 1) + matchScore
        val up         = dp(i - 1)(jj) + Gap
        val left       = dp(i)(jj - 1) + Gap
        dp(i)(jj) = math.max(diag, math.max(up, left))
        jj += 1
      }
      i += 1
    }

    // Answer = max over the final row, at the smallest attaining j.
    var maxScore = dp(m)(0)
    var maxJ     = 0
    var j        = 1
    while (j <= n) {
      if (dp(m)(j) > maxScore) {
        maxScore = dp(m)(j)
        maxJ = j
      }
      j += 1
    }

    // Traceback from (m, maxJ) until the t-prefix is fully consumed (j == 0).
    // Tie-break: diagonal > up > left. On the border i == 0, force left.
    val sSb = new StringBuilder
    val tSb = new StringBuilder
    var bi  = m
    var bj  = maxJ
    while (bj > 0) {
      val cur = dp(bi)(bj)
      if (bi == 0) {
        // Forced left: empty s remaining, consume t as a gap.
        sSb += '-'
        tSb += t.charAt(bj - 1)
        bj -= 1
      } else {
        val matchScore = if (s.charAt(bi - 1) == t.charAt(bj - 1)) 1 else Mismatch
        val diag       = dp(bi - 1)(bj - 1) + matchScore
        val up         = dp(bi - 1)(bj) + Gap
        if (cur == diag) {
          sSb += s.charAt(bi - 1)
          tSb += t.charAt(bj - 1)
          bi -= 1
          bj -= 1
        } else if (cur == up) {
          sSb += s.charAt(bi - 1)
          tSb += '-'
          bi -= 1
        } else {
          // Forced left: cur == dp(bi)(bj-1) + Gap.
          sSb += '-'
          tSb += t.charAt(bj - 1)
          bj -= 1
        }
      }
    }

    Result(
      score = maxScore,
      augmentedS = sSb.reverse.toString,
      augmentedT = tSb.reverse.toString
    )
  }
}
