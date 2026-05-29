package bio.algorithms.protein

import bio.domain.protein.{AminoAcid, GlobalAlignmentScoreProblem}

/** Computes the maximum *global alignment score* of two protein strings
  * under BLOSUM62 substitution scoring and a linear gap penalty of `-5`
  * (Rosalind spec 42 — GLOB, "Global Alignment with Scoring Matrix").
  *
  * This is the classical *Needleman-Wunsch* algorithm. Given two amino-acid
  * sequences `s` and `t`, the global alignment score is the maximum sum of
  * per-column scores over all possible alignments (sequences of matches,
  * substitutions, insertions, and deletions that consume both strings end to
  * end), where:
  *
  *   - a match or substitution column `(a, b)` contributes
  *     `Blosum62.score(a, b)`;
  *   - each gap symbol contributes `-5` (linear penalty).
  *
  * **Algorithm — classical `O(m · n)` Needleman-Wunsch DP, maximising.**
  * Build a `(m + 1) × (n + 1)` `Array[Array[Int]]` table where `dp(i)(j)` is
  * the maximum alignment score of `left[0..i)` and `right[0..j)`. Recurrence:
  *
  *   - `dp(0)(0) = 0`;
  *   - `dp(i)(0) = -5 * i` (left consumed only by deletions);
  *   - `dp(0)(j) = -5 * j` (right consumed only by insertions);
  *   - `dp(i)(j) = max(`
  *     `  dp(i-1)(j-1) + Blosum62.score(left(i-1), right(j-1)),`
  *     `  dp(i-1)(j) + (-5),  // delete from left`
  *     `  dp(i)(j-1) + (-5)   // insert into left`
  *     `)`.
  *
  * The answer is `dp(m)(n)`. Edge cases (empty `left` and/or `right`) are
  * folded into the recurrence naturally — no special-casing needed.
  *
  * **Contrast with [[EditDistance]].** Both algorithms share the same DP
  * table shape `(m+1) × (n+1)`, but:
  *
  *   - **Direction:** Levenshtein *minimises* a cost; Needleman-Wunsch
  *     *maximises* a score.
  *   - **Substitution weight:** Levenshtein uses unit cost (0 if matching,
  *     1 otherwise); Needleman-Wunsch consults the BLOSUM62 lookup.
  *   - **Gap penalty:** Levenshtein charges `+1` per gap; Needleman-Wunsch
  *     charges `-5` (still a fixed linear penalty).
  *
  * The two implementations are deliberately separate — coupling them behind
  * a `ScoringScheme` ADT would obscure both at no current gain.
  *
  * **Complexity.** `O(m · n)` time and `O(m · n)` memory. At the Rosalind
  * cap `m, n ≤ 1000` that's 10⁶ cells — milliseconds and a few MB. The
  * returned score lies in `[-5000, 11000]` (gaps bounded by `-5 * 1000` on
  * one side, BLOSUM62 max `11` per matched pair on the other), well within
  * `Int` range.
  */
object GlobalAlignmentScore {

  /** Linear gap penalty, hardcoded per Rosalind GLOB spec. */
  private val Gap: Int = -5

  def compute(problem: GlobalAlignmentScoreProblem): Int = {
    val s = problem.left.value
    val t = problem.right.value
    val m = s.length
    val n = t.length

    // Pre-resolve each character to its AminoAcid so the inner loop avoids
    // re-parsing on every cell access.
    val sAas = toAminoAcids(s)
    val tAas = toAminoAcids(t)

    val dp = Array.ofDim[Int](m + 1, n + 1)

    var i = 0
    while (i <= m) { dp(i)(0) = Gap * i; i += 1 }
    var j = 0
    while (j <= n) { dp(0)(j) = Gap * j; j += 1 }

    i = 1
    while (i <= m) {
      var jj = 1
      while (jj <= n) {
        val diag = dp(i - 1)(jj - 1) + Blosum62.score(sAas(i - 1), tAas(jj - 1))
        val up   = dp(i - 1)(jj) + Gap
        val left = dp(i)(jj - 1) + Gap
        dp(i)(jj) = math.max(diag, math.max(up, left))
        jj += 1
      }
      i += 1
    }

    dp(m)(n)
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
