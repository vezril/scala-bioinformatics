package bio.algorithms.protein

import bio.domain.protein.{AminoAcid, ConstantGapAlignmentScoreProblem}

/** Computes the maximum *global alignment score* of two protein strings
  * under BLOSUM62 substitution scoring and a *constant* gap penalty of `5`
  * (Rosalind spec 51 — GCON, "Global Alignment with Constant Gap Penalty").
  *
  * Unlike GLOB's linear gap model (`-5` per gap symbol), every gap — a
  * maximal run of contiguous insertions *or* contiguous deletions — is
  * charged a flat `5` exactly once, regardless of how many symbols it spans.
  *
  * **Algorithm — three-state (Gotoh-style) `O(m · n)` DP, maximising.**
  * Build three `(m + 1) × (n + 1)` tables over `i ∈ [0, m]` (left `s`) and
  * `j ∈ [0, n]` (right `t`), where each cell holds the best score of an
  * alignment of the prefixes `s[0..i)` and `t[0..j)` that *ends* in a given
  * terminal state:
  *
  *   - `M(i)(j)` — ends in a match/substitution column;
  *   - `X(i)(j)` — ends in a gap in `t` (a run of `s` symbols opposite `-`);
  *   - `Y(i)(j)` — ends in a gap in `s` (a run of `t` symbols opposite `-`).
  *
  * With constant gap penalty `c = 5`:
  *
  *   - `M(i)(j) = Blosum62.score(s_i, t_j) + max(M(i-1)(j-1), X(i-1)(j-1), Y(i-1)(j-1))`;
  *   - `X(i)(j) = max(M(i-1)(j) - c, X(i-1)(j), Y(i-1)(j) - c)`;
  *   - `Y(i)(j) = max(M(i)(j-1) - c, Y(i)(j-1), X(i)(j-1) - c)`.
  *
  * Gap *extension* (`X(i-1)(j)`, `Y(i)(j-1)`) carries no penalty — the `- c`
  * is paid only when a gap is *opened* (from `M`) or when the gap direction
  * *switches* (a `Y → X` or `X → Y` transition begins a distinct gap).
  *
  * **Boundaries** use a `NegInf = Int.MinValue / 4` sentinel for structurally
  * impossible states (the `/ 4` leaves headroom so `NegInf - c` cannot
  * underflow):
  *
  *   - `M(0)(0) = 0`; `M(i)(0) = NegInf` (i ≥ 1); `M(0)(j) = NegInf` (j ≥ 1);
  *   - `X(i)(0) = -c` for `i ≥ 1` (aligning `s[0..i)` to empty `t` is one
  *     gap), else `NegInf`;
  *   - `Y(0)(j) = -c` for `j ≥ 1` (symmetric), else `NegInf`.
  *
  * The answer is `max(M(m)(n), X(m)(n), Y(m)(n))`.
  *
  * **Complexity.** `O(m · n)` time and memory. At the Rosalind cap
  * `m, n ≤ 1000` that's 3·10⁶ ints — milliseconds and ~12 MB. The score is
  * bounded above by BLOSUM62's `11` per matched pair and below by the
  * (length-independent) gap charges, comfortably within `Int` range.
  */
object ConstantGapAlignmentScore {

  /** Constant gap penalty, hardcoded per Rosalind GCON spec. */
  private val Gap: Int = 5

  /** Sentinel for structurally impossible boundary states. `Int.MinValue / 4`
    * leaves headroom so `NegInf - Gap` (and `NegInf + score`) cannot underflow.
    */
  private val NegInf: Int = Int.MinValue / 4

  def compute(problem: ConstantGapAlignmentScoreProblem): Int = {
    val s = problem.left.value
    val t = problem.right.value
    val m = s.length
    val n = t.length

    val sAas = toAminoAcids(s)
    val tAas = toAminoAcids(t)

    val mm = Array.ofDim[Int](m + 1, n + 1) // ends in match/substitution
    val xx = Array.ofDim[Int](m + 1, n + 1) // ends in gap in t (run of s)
    val yy = Array.ofDim[Int](m + 1, n + 1) // ends in gap in s (run of t)

    // Boundaries.
    mm(0)(0) = 0
    xx(0)(0) = NegInf
    yy(0)(0) = NegInf

    var i = 1
    while (i <= m) {
      mm(i)(0) = NegInf
      xx(i)(0) = -Gap // s[0..i) opposite empty t is one gap, regardless of i
      yy(i)(0) = NegInf
      i += 1
    }

    var j = 1
    while (j <= n) {
      mm(0)(j) = NegInf
      xx(0)(j) = NegInf
      yy(0)(j) = -Gap // empty s opposite t[0..j) is one gap, regardless of j
      j += 1
    }

    i = 1
    while (i <= m) {
      var jj = 1
      while (jj <= n) {
        val sub = Blosum62.score(sAas(i - 1), tAas(jj - 1))
        mm(i)(jj) = sub + max3(mm(i - 1)(jj - 1), xx(i - 1)(jj - 1), yy(i - 1)(jj - 1))
        xx(i)(jj) = max3(mm(i - 1)(jj) - Gap, xx(i - 1)(jj), yy(i - 1)(jj) - Gap)
        yy(i)(jj) = max3(mm(i)(jj - 1) - Gap, yy(i)(jj - 1), xx(i)(jj - 1) - Gap)
        jj += 1
      }
      i += 1
    }

    max3(mm(m)(n), xx(m)(n), yy(m)(n))
  }

  private def max3(a: Int, b: Int, c: Int): Int = math.max(a, math.max(b, c))

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
    * are protein strings already validated by
    * [[bio.domain.protein.ProteinString]], so every character hits this map.
    */
  private val charToAa: Map[Char, AminoAcid] =
    AminoAcid.all.map(aa => aa.code -> aa).toMap
}
