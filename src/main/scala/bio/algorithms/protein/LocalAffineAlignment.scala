package bio.algorithms.protein

import bio.domain.protein.{
  AminoAcid,
  LocalAffineAlignment => LocalAffineAlignmentResult,
  LocalAffineAlignmentProblem
}

/** Computes the maximum *local alignment score* of two protein strings and one
  * optimal pair of aligned substrings under the BLOSUM62 substitution matrix
  * and an *affine* gap penalty (Rosalind spec 53 — LAFF, "Local Alignment with
  * Scoring Matrix and Affine Gap Penalty").
  *
  * A gap of length `L` costs `a + b·(L − 1)`, where `a = 11` is the
  * gap-opening penalty (charged once for the first gap symbol) and `b = 1`
  * is the gap-extension penalty (charged for each subsequent symbol). LAFF is
  * GAFF's affine three-state model made *local* in the Smith-Waterman sense.
  *
  * **Algorithm — local three-state (Gotoh + Smith-Waterman) `O(m · n)` DP +
  * traceback.** Build three `(m + 1) × (n + 1)` tables over `i ∈ [0, m]`
  * (left `s`) and `j ∈ [0, n]` (right `t`); each cell holds the best score of
  * a *local* alignment of suffixes of the prefixes `s[0..i)` / `t[0..j)` that
  * *ends* in a given terminal state:
  *
  *   - `M(i)(j)` — ends in a match/substitution column, **clamped at 0** so a
  *     local alignment may always restart;
  *   - `X(i)(j)` — ends in a gap in `t` (a run of `s` symbols opposite `-`);
  *   - `Y(i)(j)` — ends in a gap in `s` (a run of `t` symbols opposite `-`).
  *
  * With `a = 11` (open) and `b = 1` (extend):
  *
  *   - `M(i)(j) = max(0, Blosum62.score(s_i, t_j) + max(M(i-1)(j-1), X(i-1)(j-1), Y(i-1)(j-1)))`;
  *   - `X(i)(j) = max(M(i-1)(j) - a, X(i-1)(j) - b, Y(i-1)(j) - a)`;
  *   - `Y(i)(j) = max(M(i)(j-1) - a, Y(i)(j-1) - b, X(i)(j-1) - a)`.
  *
  * Only the match state is clamped; the gap states carry their accrued
  * (negative) penalty until a following match either rescues them or the
  * surrounding `M` restarts at 0.
  *
  * **Boundaries** use `NegInf = Int.MinValue / 4` for the gap states (a local
  * alignment never begins with a gap) while the match boundaries are the
  * 0-clamp:
  *
  *   - `M(i)(0) = M(0)(j) = M(0)(0) = 0`;
  *   - `X(i)(0) = X(0)(j) = NegInf`;
  *   - `Y(i)(0) = Y(0)(j) = NegInf`.
  *
  * The score is the maximum over *all* `M(i)(j)` cells (Smith-Waterman tracks a
  * running maximum, not just the corner). One optimal substring pair is
  * recovered by traceback from the maximising `(maxI, maxJ)` in state `M`,
  * stopping at a 0-clamped match cell. Because each step consumes `s` (in `M`
  * and `X`) and/or `t` (in `M` and `Y`) and the visited indices form a
  * contiguous descending range, the substrings are simply
  * `s.substring(startI, maxI)` and `t.substring(startJ, maxJ)`.
  *
  * **Output type.** The substrings are plain `String`s — contiguous regions of
  * the inputs containing no `-` gap symbol.
  *
  * **Complexity.** `O(m · n)` time and memory. At the LAFF cap `m, n ≤ 10000`
  * the three tables are ~3·10⁸ ints ≈ 1.2 GB worst case; real datasets are far
  * smaller. The cap is a validation guard, not a sizing target.
  */
object LocalAffineAlignment {

  /** Gap-opening penalty, hardcoded per Rosalind LAFF spec. */
  private val Open: Int = 11

  /** Gap-extension penalty, hardcoded per Rosalind LAFF spec. */
  private val Extend: Int = 1

  /** Sentinel for structurally impossible gap-state boundaries. */
  private val NegInf: Int = Int.MinValue / 4

  // Traceback state tags.
  private val StateM = 0 // match/substitution
  private val StateX = 1 // gap in right (s opposite '-')
  private val StateY = 2 // gap in left  ('-' opposite t)

  def compute(problem: LocalAffineAlignmentProblem): LocalAffineAlignmentResult = {
    val s = problem.left.value
    val t = problem.right.value
    val m = s.length
    val n = t.length

    if (m == 0 || n == 0) return LocalAffineAlignmentResult(0, "", "")

    val sAas = toAminoAcids(s)
    val tAas = toAminoAcids(t)

    val mm = Array.ofDim[Int](m + 1, n + 1) // ends in match/substitution (clamped >= 0)
    val xx = Array.ofDim[Int](m + 1, n + 1) // ends in gap in t (run of s)
    val yy = Array.ofDim[Int](m + 1, n + 1) // ends in gap in s (run of t)

    // Boundaries: match clamps to 0, gap states are structurally impossible.
    mm(0)(0) = 0
    xx(0)(0) = NegInf
    yy(0)(0) = NegInf

    var i = 1
    while (i <= m) {
      mm(i)(0) = 0
      xx(i)(0) = NegInf
      yy(i)(0) = NegInf
      i += 1
    }

    var j = 1
    while (j <= n) {
      mm(0)(j) = 0
      xx(0)(j) = NegInf
      yy(0)(j) = NegInf
      j += 1
    }

    var maxScore = 0
    var maxI     = 0
    var maxJ     = 0

    i = 1
    while (i <= m) {
      var jj = 1
      while (jj <= n) {
        val sub = Blosum62.score(sAas(i - 1), tAas(jj - 1))
        val diag = sub + max3(mm(i - 1)(jj - 1), xx(i - 1)(jj - 1), yy(i - 1)(jj - 1))
        mm(i)(jj) = math.max(0, diag)
        xx(i)(jj) = max3(mm(i - 1)(jj) - Open, xx(i - 1)(jj) - Extend, yy(i - 1)(jj) - Open)
        yy(i)(jj) = max3(mm(i)(jj - 1) - Open, yy(i)(jj - 1) - Extend, xx(i)(jj - 1) - Open)
        if (mm(i)(jj) > maxScore) {
          maxScore = mm(i)(jj)
          maxI = i
          maxJ = jj
        }
        jj += 1
      }
      i += 1
    }

    // Traceback from the maximising match cell to its 0-clamped restart, in
    // order to locate the start of the aligned region. Tie-break mirrors GAFF:
    // prefer match/diagonal, then extending the current gap, then open/switch.
    var bi    = maxI
    var bj    = maxJ
    var state = StateM

    while (!(state == StateM && mm(bi)(bj) == 0)) {
      state match {
        case StateM =>
          val sub    = Blosum62.score(sAas(bi - 1), tAas(bj - 1))
          val target = mm(bi)(bj) - sub
          bi -= 1
          bj -= 1
          state =
            if (mm(bi)(bj) == target) StateM
            else if (xx(bi)(bj) == target) StateX
            else StateY

        case StateX =>
          val target = xx(bi)(bj)
          bi -= 1
          state =
            if (xx(bi)(bj) - Extend == target) StateX
            else if (mm(bi)(bj) - Open == target) StateM
            else StateY

        case _ => // StateY
          val target = yy(bi)(bj)
          bj -= 1
          state =
            if (yy(bi)(bj) - Extend == target) StateY
            else if (mm(bi)(bj) - Open == target) StateM
            else StateX
      }
    }

    LocalAffineAlignmentResult(
      score = maxScore,
      leftSubstring = s.substring(bi, maxI),
      rightSubstring = t.substring(bj, maxJ)
    )
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

  /** Total `Char => AminoAcid` map for the 20 standard amino acids. */
  private val charToAa: Map[Char, AminoAcid] =
    AminoAcid.all.map(aa => aa.code -> aa).toMap
}
