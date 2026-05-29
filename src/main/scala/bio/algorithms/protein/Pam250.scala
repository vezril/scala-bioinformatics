package bio.algorithms.protein

import bio.domain.protein.AminoAcid

/** The canonical PAM250 amino-acid substitution-score matrix.
  *
  * PAM250 is the Point Accepted Mutation matrix scaled to 250 evolutionary
  * distance — the standard "deep homology" substitution matrix, used here by
  * Rosalind LOCA for local alignment scoring. Like BLOSUM62, it's a 20 × 20
  * symmetric integer matrix; unlike BLOSUM62, PAM250 emphasises substitutions
  * tolerable over long evolutionary spans.
  *
  * The matrix is symmetric — `score(a, b) == score(b, a)` for every pair.
  *
  * **Encoding.** Internally, the 20 × 20 matrix is stored as a dense
  * `Array[Array[Int]]` indexed by amino-acid *canonical ordinal*
  * (A R N D C Q E G H I L K M F P S T W Y V — the same row/column order as
  * [[Blosum62]] and the NCBI distribution). A `private val ordinal:
  * AminoAcid => Int` maps the project's [[AminoAcid]] ADT values to those
  * ordinals. Lookups are `O(1)` with no boxing, suitable for hot-path use
  * from inside DP inner loops.
  */
object Pam250 {

  // Canonical NCBI ordering (rows and columns) — same as Blosum62.
  private val canonicalOrder: Vector[AminoAcid] = Vector(
    AminoAcid.A, AminoAcid.R, AminoAcid.N, AminoAcid.D, AminoAcid.C,
    AminoAcid.Q, AminoAcid.E, AminoAcid.G, AminoAcid.H, AminoAcid.I,
    AminoAcid.L, AminoAcid.K, AminoAcid.M, AminoAcid.F, AminoAcid.P,
    AminoAcid.S, AminoAcid.T, AminoAcid.W, AminoAcid.Y, AminoAcid.V
  )

  // Map each amino acid to its canonical ordinal once at init.
  private val ordinal: Map[AminoAcid, Int] =
    canonicalOrder.zipWithIndex.toMap

  // The 20 × 20 PAM250 matrix, in canonicalOrder × canonicalOrder.
  //              A   R   N   D   C   Q   E   G   H   I   L   K   M   F   P   S   T   W   Y   V
  private val matrix: Array[Array[Int]] = Array(
    /* A */ Array( 2, -2,  0,  0, -2,  0,  0,  1, -1, -1, -2, -1, -1, -3,  1,  1,  1, -6, -3,  0),
    /* R */ Array(-2,  6,  0, -1, -4,  1, -1, -3,  2, -2, -3,  3,  0, -4,  0,  0, -1,  2, -4, -2),
    /* N */ Array( 0,  0,  2,  2, -4,  1,  1,  0,  2, -2, -3,  1, -2, -3,  0,  1,  0, -4, -2, -2),
    /* D */ Array( 0, -1,  2,  4, -5,  2,  3,  1,  1, -2, -4,  0, -3, -6, -1,  0,  0, -7, -4, -2),
    /* C */ Array(-2, -4, -4, -5, 12, -5, -5, -3, -3, -2, -6, -5, -5, -4, -3,  0, -2, -8,  0, -2),
    /* Q */ Array( 0,  1,  1,  2, -5,  4,  2, -1,  3, -2, -2,  1, -1, -5,  0, -1, -1, -5, -4, -2),
    /* E */ Array( 0, -1,  1,  3, -5,  2,  4,  0,  1, -2, -3,  0, -2, -5, -1,  0,  0, -7, -4, -2),
    /* G */ Array( 1, -3,  0,  1, -3, -1,  0,  5, -2, -3, -4, -2, -3, -5,  0,  1,  0, -7, -5, -1),
    /* H */ Array(-1,  2,  2,  1, -3,  3,  1, -2,  6, -2, -2,  0, -2, -2,  0, -1, -1, -3,  0, -2),
    /* I */ Array(-1, -2, -2, -2, -2, -2, -2, -3, -2,  5,  2, -2,  2,  1, -2, -1,  0, -5, -1,  4),
    /* L */ Array(-2, -3, -3, -4, -6, -2, -3, -4, -2,  2,  6, -3,  4,  2, -3, -3, -2, -2, -1,  2),
    /* K */ Array(-1,  3,  1,  0, -5,  1,  0, -2,  0, -2, -3,  5,  0, -5, -1,  0,  0, -3, -4, -2),
    /* M */ Array(-1,  0, -2, -3, -5, -1, -2, -3, -2,  2,  4,  0,  6,  0, -2, -2, -1, -4, -2,  2),
    /* F */ Array(-3, -4, -3, -6, -4, -5, -5, -5, -2,  1,  2, -5,  0,  9, -5, -3, -3,  0,  7, -1),
    /* P */ Array( 1,  0,  0, -1, -3,  0, -1,  0,  0, -2, -3, -1, -2, -5,  6,  1,  0, -6, -5, -1),
    /* S */ Array( 1,  0,  1,  0,  0, -1,  0,  1, -1, -1, -3,  0, -2, -3,  1,  2,  1, -2, -3, -1),
    /* T */ Array( 1, -1,  0,  0, -2, -1,  0,  0, -1,  0, -2,  0, -1, -3,  0,  1,  3, -5, -3,  0),
    /* W */ Array(-6,  2, -4, -7, -8, -5, -7, -7, -3, -5, -2, -3, -4,  0, -6, -2, -5, 17,  0, -6),
    /* Y */ Array(-3, -4, -2, -4,  0, -4, -4, -5,  0, -1, -1, -4, -2,  7, -5, -3, -3,  0, 10, -2),
    /* V */ Array( 0, -2, -2, -2, -2, -2, -2, -1, -2,  4,  2, -2,  2, -1, -1, -1,  0, -6, -2,  4)
  )

  /** PAM250 substitution score for the amino-acid pair `(a, b)`.
    *
    * Symmetric: `score(a, b) == score(b, a)` for every pair.
    */
  def score(a: AminoAcid, b: AminoAcid): Int =
    matrix(ordinal(a))(ordinal(b))
}
