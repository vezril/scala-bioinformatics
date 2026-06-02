package bio.algorithms.analysis

import bio.domain.analysis.{
  SemiglobalAlignment => Result,
  SemiglobalAlignmentProblem
}

/** Computes one optimal *semiglobal alignment* of all of `s` against all of `t`
  * under the score (`+1` match, `-1` substitution, `-1` scored gap), with gap
  * runs at the **leading or trailing ends of either string free** — Rosalind
  * SMGB ("Semiglobal Alignment").
  *
  * Semiglobal alignment is the **symmetric** free-end-gap member of the alignment
  * taxonomy: both strings are fully present, but the gap runs at the very start
  * and very end cost nothing. Contrast:
  *
  *   - **Global** (GLOB/EDTA): all of `s` against all of `t`, end gaps *penalized*.
  *   - **Local** (LOCA): a substring of `s` against a substring of `t`.
  *   - **Fitting** (SIMS): a substring of `s` against *all* of `t`.
  *   - **Overlap** (OAP): a *suffix* of `s` against a *prefix* of `t` — one-sided.
  *   - **Semiglobal** (this): all of `s` against all of `t`, both end-gap runs free.
  *
  * **Algorithm — classical `O(m · n)` DP + traceback.** Build `dp(i)(j)` over
  * `i ∈ [0, m]` (rows = `s`) and `j ∈ [0, n]` (cols = `t`):
  *
  *   - `dp(i)(0) = 0` and `dp(0)(j) = 0` — a *leading* gap run in either string is
  *     free (consuming a prefix of one string against gaps in the other is free).
  *   - `dp(i)(j) = max(dp(i-1)(j-1) + (if s(i-1) == t(j-1) then +1 else -1),
  *     dp(i-1)(j) - 1, dp(i)(j-1) - 1)`.
  *
  * The optimum is the maximum cell over the **last row** `dp(m)(j)` and the
  * **last column** `dp(i)(n)` — a *trailing* gap run in either string is free, so
  * the scored path may end anywhere along the bottom or right edge, the rest of
  * the corner reached by free trailing gaps. The best cell is chosen
  * deterministically (scan the last row left→right then the last column
  * top→bottom, keeping the first strict maximum).
  *
  * **Traceback** builds both strings right-to-left into reversed builders:
  *
  *   1. **Trailing free gaps** from the corner `(m, n)` to the best cell
  *      `(bi, bj)`: if `bi == m` emit `t(n-1 … bj)` against `-`; if `bj == n` emit
  *      `s(m-1 … bi)` against `-`. (At most one run; neither when best == corner.)
  *   2. **Core scored traceback** from `(bi, bj)` while `i > 0 && j > 0`, tie-break
  *      **diagonal > up > left**.
  *   3. **Leading free gaps** once an edge is hit: while `j > 0` emit `-`/`t(j-1)`;
  *      then while `i > 0` emit `s(i-1)`/`-`.
  *
  * Both builders are reversed once at the end. Every character of `s` and `t` is
  * emitted (as a match/substitution or against a free gap), so the augmented
  * strings, with gap symbols removed, reproduce `s` and `t` exactly.
  *
  * **Complexity.** `O(m · n)` time and memory. Traceback needs the full table, so
  * a row-rolling optimisation is not applicable. The imperative kernel
  * (`var`/`while`/`Array`) is the established convention for this project's
  * alignment family; the public signature stays pure and total.
  */
object SemiglobalAlignment {

  /** Linear gap penalty (per scored inserted/deleted symbol), per SMGB score. */
  private val Gap: Int = -1

  /** Substitution penalty (per mismatched symbol), per SMGB score. */
  private val Mismatch: Int = -1

  def align(problem: SemiglobalAlignmentProblem): Result = {
    val s = problem.s.value
    val t = problem.t.value
    val m = s.length
    val n = t.length

    val dp = Array.ofDim[Int](m + 1, n + 1)
    // dp(i)(0) = 0 and dp(0)(j) = 0 (Array.ofDim default) — free leading gaps.

    var i = 1
    while (i <= m) {
      val si = s.charAt(i - 1)
      var j = 1
      while (j <= n) {
        val matchScore = if (si == t.charAt(j - 1)) 1 else Mismatch
        val diag       = dp(i - 1)(j - 1) + matchScore
        val up         = dp(i - 1)(j) + Gap
        val left       = dp(i)(j - 1) + Gap
        dp(i)(j) = math.max(diag, math.max(up, left))
        j += 1
      }
      i += 1
    }

    // Answer = max over the last row then the last column, first strict maximum.
    var maxScore = dp(m)(0)
    var bi       = m
    var bj       = 0
    var jr       = 1
    while (jr <= n) {
      if (dp(m)(jr) > maxScore) {
        maxScore = dp(m)(jr)
        bi = m
        bj = jr
      }
      jr += 1
    }
    var ic = 0
    while (ic <= m) {
      if (dp(ic)(n) > maxScore) {
        maxScore = dp(ic)(n)
        bi = ic
        bj = n
      }
      ic += 1
    }

    val sSb = new StringBuilder
    val tSb = new StringBuilder

    // 1. Trailing free gaps from the corner (m, n) down to the best cell.
    if (bi == m) {
      var k = n
      while (k > bj) {
        sSb += '-'
        tSb += t.charAt(k - 1)
        k -= 1
      }
    } else { // bj == n
      var k = m
      while (k > bi) {
        sSb += s.charAt(k - 1)
        tSb += '-'
        k -= 1
      }
    }

    // 2. Core scored traceback; tie-break diagonal > up > left.
    while (bi > 0 && bj > 0) {
      val cur        = dp(bi)(bj)
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
        sSb += '-'
        tSb += t.charAt(bj - 1)
        bj -= 1
      }
    }

    // 3. Leading free gaps: consume the remaining prefix of t, then of s.
    while (bj > 0) {
      sSb += '-'
      tSb += t.charAt(bj - 1)
      bj -= 1
    }
    while (bi > 0) {
      sSb += s.charAt(bi - 1)
      tSb += '-'
      bi -= 1
    }

    Result(
      score = maxScore,
      augmentedS = sSb.reverse.toString,
      augmentedT = tSb.reverse.toString
    )
  }
}
