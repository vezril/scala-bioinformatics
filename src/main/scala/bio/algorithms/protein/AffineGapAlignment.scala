package bio.algorithms.protein

import bio.domain.protein.{
  AffineGapAlignment => AffineGapAlignmentResult,
  AffineGapAlignmentProblem,
  AminoAcid
}

/** Computes the maximum *global alignment score* of two protein strings and
  * one optimal alignment under the BLOSUM62 substitution matrix and an
  * *affine* gap penalty (Rosalind spec 52 — GAFF, "Global Alignment with
  * Scoring Matrix and Affine Gap Penalty").
  *
  * A gap of length `L` costs `a + b·(L − 1)`, where `a = 11` is the
  * gap-opening penalty (charged once for the first gap symbol) and `b = 1`
  * is the gap-extension penalty (charged for each subsequent symbol). This
  * generalises GLOB's linear gap (`b = a`) and GCON's constant gap (`b = 0`).
  *
  * **Algorithm — three-state (Gotoh-style) `O(m · n)` DP + traceback,
  * maximising.** Build three `(m + 1) × (n + 1)` tables over `i ∈ [0, m]`
  * (left `s`) and `j ∈ [0, n]` (right `t`); each cell holds the best score
  * of an alignment of the prefixes `s[0..i)` and `t[0..j)` that *ends* in a
  * given terminal state:
  *
  *   - `M(i)(j)` — ends in a match/substitution column;
  *   - `X(i)(j)` — ends in a gap in `t` (a run of `s` symbols opposite `-`);
  *   - `Y(i)(j)` — ends in a gap in `s` (a run of `t` symbols opposite `-`).
  *
  * With `a = 11` (open) and `b = 1` (extend):
  *
  *   - `M(i)(j) = Blosum62.score(s_i, t_j) + max(M(i-1)(j-1), X(i-1)(j-1), Y(i-1)(j-1))`;
  *   - `X(i)(j) = max(M(i-1)(j) - a, X(i-1)(j) - b, Y(i-1)(j) - a)`;
  *   - `Y(i)(j) = max(M(i)(j-1) - a, Y(i)(j-1) - b, X(i)(j-1) - a)`.
  *
  * Opening a gap (from `M`) or switching gap direction (a `Y → X` or
  * `X → Y` transition, which begins a distinct gap) costs `-a`; extending an
  * existing gap costs `-b`.
  *
  * **Boundaries** use a `NegInf = Int.MinValue / 4` sentinel for
  * structurally impossible states (the `/ 4` leaves headroom so `NegInf - a`
  * cannot underflow):
  *
  *   - `M(0)(0) = 0`; `M(i)(0) = NegInf` (i ≥ 1); `M(0)(j) = NegInf` (j ≥ 1);
  *   - `X(i)(0) = -(a + b·(i-1))` for `i ≥ 1` (one gap spanning `s[0..i)`),
  *     else `NegInf`;
  *   - `Y(0)(j) = -(a + b·(j-1))` for `j ≥ 1` (symmetric), else `NegInf`.
  *
  * The score is `max(M(m)(n), X(m)(n), Y(m)(n))`. One optimal alignment is
  * recovered by traceback from `(m, n)` in the maximal state, emitting one
  * column per step into two reversed builders. **Tie-break order:** prefer a
  * match/diagonal, then extending the current gap (staying in the same gap
  * state), then opening/switching. This keeps gaps contiguous (affine favours
  * one long gap over several short ones) and reproduces the canonical
  * published sample alignment `PRT---EINS` / `PRTWPSEIN-`.
  *
  * **Output type.** The augmented strings are plain `String` (not
  * `ProteinString`) because they contain the `-` gap symbol, which is not a
  * valid amino-acid code.
  *
  * **Complexity.** `O(m · n)` time and memory. At the GAFF cap `m, n ≤ 100`
  * that's ~10⁴ cells per table — microseconds and a few KB.
  */
object AffineGapAlignment {

  /** Gap-opening penalty, hardcoded per Rosalind GAFF spec. */
  private val Open: Int = 11

  /** Gap-extension penalty, hardcoded per Rosalind GAFF spec. */
  private val Extend: Int = 1

  /** Sentinel for structurally impossible boundary states. `Int.MinValue / 4`
    * leaves headroom so `NegInf - Open` (and `NegInf + score`) cannot underflow.
    */
  private val NegInf: Int = Int.MinValue / 4

  // Traceback state tags.
  private val StateM = 0 // match/substitution
  private val StateX = 1 // gap in right (s opposite '-')
  private val StateY = 2 // gap in left  ('-' opposite t)

  def compute(problem: AffineGapAlignmentProblem): AffineGapAlignmentResult = {
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
      xx(i)(0) = -(Open + Extend * (i - 1)) // one gap-in-t spanning s[0..i)
      yy(i)(0) = NegInf
      i += 1
    }

    var j = 1
    while (j <= n) {
      mm(0)(j) = NegInf
      xx(0)(j) = NegInf
      yy(0)(j) = -(Open + Extend * (j - 1)) // one gap-in-s spanning t[0..j)
      j += 1
    }

    i = 1
    while (i <= m) {
      var jj = 1
      while (jj <= n) {
        val sub = Blosum62.score(sAas(i - 1), tAas(jj - 1))
        mm(i)(jj) = sub + max3(mm(i - 1)(jj - 1), xx(i - 1)(jj - 1), yy(i - 1)(jj - 1))
        xx(i)(jj) = max3(mm(i - 1)(jj) - Open, xx(i - 1)(jj) - Extend, yy(i - 1)(jj) - Open)
        yy(i)(jj) = max3(mm(i)(jj - 1) - Open, yy(i)(jj - 1) - Extend, xx(i)(jj - 1) - Open)
        jj += 1
      }
      i += 1
    }

    val finalScore = max3(mm(m)(n), xx(m)(n), yy(m)(n))

    // Traceback. Build augmented strings reversed, then reverse once.
    val leftSb  = new StringBuilder
    val rightSb = new StringBuilder
    var bi      = m
    var bj      = n

    // Pick the starting state: prefer match, then gap-in-right, then gap-in-left.
    var state =
      if (mm(bi)(bj) == finalScore) StateM
      else if (xx(bi)(bj) == finalScore) StateX
      else StateY

    while (bi > 0 || bj > 0) {
      state match {
        case StateM =>
          // Match column: emit (s[bi-1], t[bj-1]), step diagonally.
          leftSb += s.charAt(bi - 1)
          rightSb += t.charAt(bj - 1)
          val target = mm(bi)(bj) - Blosum62.score(sAas(bi - 1), tAas(bj - 1))
          bi -= 1
          bj -= 1
          // Predecessor state: prefer match, then gap-in-right, then gap-in-left.
          state =
            if (mm(bi)(bj) == target) StateM
            else if (xx(bi)(bj) == target) StateX
            else StateY

        case StateX =>
          // Gap in right: emit (s[bi-1], '-'), step up.
          leftSb += s.charAt(bi - 1)
          rightSb += '-'
          val target = xx(bi)(bj)
          bi -= 1
          // Prefer extend (stay in X), then open (from M), then switch (from Y).
          state =
            if (xx(bi)(bj) - Extend == target) StateX
            else if (mm(bi)(bj) - Open == target) StateM
            else StateY

        case _ => // StateY
          // Gap in left: emit ('-', t[bj-1]), step left.
          leftSb += '-'
          rightSb += t.charAt(bj - 1)
          val target = yy(bi)(bj)
          bj -= 1
          // Prefer extend (stay in Y), then open (from M), then switch (from X).
          state =
            if (yy(bi)(bj) - Extend == target) StateY
            else if (mm(bi)(bj) - Open == target) StateM
            else StateX
      }
    }

    AffineGapAlignmentResult(
      score = finalScore,
      augmentedLeft = leftSb.reverse.toString,
      augmentedRight = rightSb.reverse.toString
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

  /** Total `Char => AminoAcid` map for the 20 standard amino acids. Inputs
    * are protein strings already validated by
    * [[bio.domain.protein.ProteinString]], so every character hits this map.
    */
  private val charToAa: Map[Char, AminoAcid] =
    AminoAcid.all.map(aa => aa.code -> aa).toMap
}
