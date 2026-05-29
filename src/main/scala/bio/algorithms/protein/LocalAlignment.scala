package bio.algorithms.protein

import bio.domain.protein.{
  AminoAcid,
  LocalAlignment => Result,
  LocalAlignmentProblem
}

/** Computes the maximum *local alignment score* of two protein strings under
  * PAM250 substitution scoring + linear gap penalty `-5`, and recovers the
  * two substrings that achieve it (Rosalind spec 47 — LOCA, "Local
  * Alignment with Scoring Matrix").
  *
  * This is the classical *Smith-Waterman* algorithm. Where Needleman-Wunsch
  * (see [[GlobalAlignmentScore]]) aligns the entire pair of strings,
  * Smith-Waterman finds the *substrings* `r ⊆ s` and `u ⊆ t` whose pairwise
  * alignment score is maximised. Three structural differences from
  * Needleman-Wunsch:
  *
  *   1. **0-clamp at every cell.** Every cell can either extend a
  *      positive-scoring alignment or "start fresh" with score `0`.
  *   2. **Boundary conditions are zeros**, not gap-penalty multiples.
  *   3. **Traceback starts from the global max cell**, not the bottom-right,
  *      and stops when the cell value drops to `0`.
  *
  * **Algorithm — classical `O(m · n)` Smith-Waterman DP + traceback.**
  *
  *   - `dp(0)(j) = dp(i)(0) = 0`;
  *   - `dp(i)(j) = max(0, dp(i-1)(j-1) + Pam250(s(i-1), t(j-1)), dp(i-1)(j) + (-5), dp(i)(j-1) + (-5))`;
  *   - track the running global max `(maxScore, maxI, maxJ)`.
  *
  * Traceback from `(maxI, maxJ)` follows the move that achieved each cell
  * value, preferring **up > left > diagonal** on ties. (Preferring indels
  * over substitution on cost-ties pushes the alignment toward shorter,
  * more "compact" substrings — preferring a fresh L-L restart over an
  * extending T-Y substitution at a tied cell — which matches the canonical
  * Rosalind sample's published output.) Stops when the current cell value
  * is `0`. Emits per move:
  *
  *   - **diagonal**: append `s(i-1)` to the left builder and `t(j-1)` to the
  *     right builder; decrement both indices.
  *   - **up**: append `s(i-1)` to the left builder only; decrement `i`.
  *   - **left**: append `t(j-1)` to the right builder only; decrement `j`.
  *
  * Both builders are reversed once at the end to recover the substrings in
  * input order. The output substrings are plain `String`s without `-` gap
  * symbols — they are the contiguous regions of the original inputs, not
  * augmented alignment strings.
  *
  * **Complexity.** `O(m · n)` time and memory. At the Rosalind cap
  * `m, n ≤ 1000` that's `~10^6` cells — milliseconds and a few MB.
  */
object LocalAlignment {

  /** Linear gap penalty, hardcoded per Rosalind LOCA spec. */
  private val Gap: Int = -5

  def compute(problem: LocalAlignmentProblem): Result = {
    val s = problem.left.value
    val t = problem.right.value
    val m = s.length
    val n = t.length

    if (m == 0 || n == 0) return Result(0, "", "")

    // Pre-resolve each character to its AminoAcid so the inner loop avoids
    // re-parsing on every cell access.
    val sAas = toAminoAcids(s)
    val tAas = toAminoAcids(t)

    val dp = Array.ofDim[Int](m + 1, n + 1)
    // dp(0)(_) and dp(_)(0) are zero by Array.ofDim default; no init pass needed.

    var maxScore = 0
    var maxI     = 0
    var maxJ     = 0

    var i = 1
    while (i <= m) {
      var jj = 1
      while (jj <= n) {
        val diag = dp(i - 1)(jj - 1) + Pam250.score(sAas(i - 1), tAas(jj - 1))
        val up   = dp(i - 1)(jj) + Gap
        val left = dp(i)(jj - 1) + Gap
        val cell = math.max(0, math.max(diag, math.max(up, left)))
        dp(i)(jj) = cell
        if (cell > maxScore) {
          maxScore = cell
          maxI = i
          maxJ = jj
        }
        jj += 1
      }
      i += 1
    }

    // Traceback from the global max cell.
    // Tie-break order: up > left > diagonal (prefer indels over substitution
    // so the alignment stays compact and matches Rosalind's published sample).
    val leftSb  = new StringBuilder
    val rightSb = new StringBuilder
    var bi      = maxI
    var bj      = maxJ
    while (bi > 0 && bj > 0 && dp(bi)(bj) > 0) {
      val cur  = dp(bi)(bj)
      val up   = dp(bi - 1)(bj) + Gap
      val left = dp(bi)(bj - 1) + Gap

      if (cur == up) {
        leftSb += s.charAt(bi - 1)
        bi -= 1
      } else if (cur == left) {
        rightSb += t.charAt(bj - 1)
        bj -= 1
      } else {
        // Forced diagonal: cur == dp(bi-1)(bj-1) + Pam250.score(...).
        leftSb += s.charAt(bi - 1)
        rightSb += t.charAt(bj - 1)
        bi -= 1
        bj -= 1
      }
    }

    Result(
      score = maxScore,
      leftSubstring = leftSb.reverse.toString,
      rightSubstring = rightSb.reverse.toString
    )
  }

  /** Resolve each character of a (presumed-valid) protein string to its
    * [[AminoAcid]] ADT value via a one-shot `Char => AminoAcid` lookup.
    */
  private def toAminoAcids(s: String): Array[AminoAcid] = {
    val out = new Array[AminoAcid](s.length)
    var k   = 0
    while (k < s.length) {
      out(k) = charToAa(s.charAt(k))
      k += 1
    }
    out
  }

  /** Total `Char => AminoAcid` map for the 20 standard amino acids. Inputs
    * are protein strings already validated by [[bio.domain.protein.ProteinString]],
    * so every character must hit this map.
    */
  private val charToAa: Map[Char, AminoAcid] =
    AminoAcid.all.map(aa => aa.code -> aa).toMap
}
