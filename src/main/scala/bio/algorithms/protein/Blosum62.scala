package bio.algorithms.protein

import bio.domain.protein.AminoAcid

/** The canonical NCBI BLOSUM62 amino-acid substitution-score matrix.
  *
  * BLOSUM62 is the standard substitution matrix used in protein alignment
  * algorithms (Needleman-Wunsch, Smith-Waterman, BLAST). It assigns an
  * integer score to every ordered pair of the 20 standard amino acids,
  * reflecting how often that substitution is observed in homologous protein
  * sequences clustered at ≥ 62% identity. Positive scores indicate
  * conservative substitutions (likely homologous); negative scores indicate
  * disfavoured substitutions.
  *
  * The matrix is symmetric — `score(a, b) == score(b, a)` for every pair.
  *
  * **Encoding.** Internally, the 20 × 20 matrix is stored as a dense
  * `Array[Array[Int]]` indexed by amino-acid *canonical-BLOSUM62 ordinal*
  * (A R N D C Q E G H I L K M F P S T W Y V — the row/column order of the
  * NCBI distribution). A `private val ordinal: AminoAcid => Int` maps the
  * project's [[AminoAcid]] ADT values to those ordinals. Lookups are `O(1)`
  * with no boxing, suitable for hot-path use from inside DP inner loops.
  */
object Blosum62 {

  // Canonical NCBI BLOSUM62 ordering (rows and columns).
  private val canonicalOrder: Vector[AminoAcid] = Vector(
    AminoAcid.A, AminoAcid.R, AminoAcid.N, AminoAcid.D, AminoAcid.C,
    AminoAcid.Q, AminoAcid.E, AminoAcid.G, AminoAcid.H, AminoAcid.I,
    AminoAcid.L, AminoAcid.K, AminoAcid.M, AminoAcid.F, AminoAcid.P,
    AminoAcid.S, AminoAcid.T, AminoAcid.W, AminoAcid.Y, AminoAcid.V
  )

  // Map each amino acid to its canonical-BLOSUM62 ordinal once at init.
  private val ordinal: Map[AminoAcid, Int] =
    canonicalOrder.zipWithIndex.toMap

  // The 20 × 20 NCBI BLOSUM62 matrix, in canonicalOrder × canonicalOrder.
  //              A   R   N   D   C   Q   E   G   H   I   L   K   M   F   P   S   T   W   Y   V
  private val matrix: Array[Array[Int]] = Array(
    /* A */ Array( 4, -1, -2, -2,  0, -1, -1,  0, -2, -1, -1, -1, -1, -2, -1,  1,  0, -3, -2,  0),
    /* R */ Array(-1,  5,  0, -2, -3,  1,  0, -2,  0, -3, -2,  2, -1, -3, -2, -1, -1, -3, -2, -3),
    /* N */ Array(-2,  0,  6,  1, -3,  0,  0,  0,  1, -3, -3,  0, -2, -3, -2,  1,  0, -4, -2, -3),
    /* D */ Array(-2, -2,  1,  6, -3,  0,  2, -1, -1, -3, -4, -1, -3, -3, -1,  0, -1, -4, -3, -3),
    /* C */ Array( 0, -3, -3, -3,  9, -3, -4, -3, -3, -1, -1, -3, -1, -2, -3, -1, -1, -2, -2, -1),
    /* Q */ Array(-1,  1,  0,  0, -3,  5,  2, -2,  0, -3, -2,  1,  0, -3, -1,  0, -1, -2, -1, -2),
    /* E */ Array(-1,  0,  0,  2, -4,  2,  5, -2,  0, -3, -3,  1, -2, -3, -1,  0, -1, -3, -2, -2),
    /* G */ Array( 0, -2,  0, -1, -3, -2, -2,  6, -2, -4, -4, -2, -3, -3, -2,  0, -2, -2, -3, -3),
    /* H */ Array(-2,  0,  1, -1, -3,  0,  0, -2,  8, -3, -3, -1, -2, -1, -2, -1, -2, -2,  2, -3),
    /* I */ Array(-1, -3, -3, -3, -1, -3, -3, -4, -3,  4,  2, -3,  1,  0, -3, -2, -1, -3, -1,  3),
    /* L */ Array(-1, -2, -3, -4, -1, -2, -3, -4, -3,  2,  4, -2,  2,  0, -3, -2, -1, -2, -1,  1),
    /* K */ Array(-1,  2,  0, -1, -3,  1,  1, -2, -1, -3, -2,  5, -1, -3, -1,  0, -1, -3, -2, -2),
    /* M */ Array(-1, -1, -2, -3, -1,  0, -2, -3, -2,  1,  2, -1,  5,  0, -2, -1, -1, -1, -1,  1),
    /* F */ Array(-2, -3, -3, -3, -2, -3, -3, -3, -1,  0,  0, -3,  0,  6, -4, -2, -2,  1,  3, -1),
    /* P */ Array(-1, -2, -2, -1, -3, -1, -1, -2, -2, -3, -3, -1, -2, -4,  7, -1, -1, -4, -3, -2),
    /* S */ Array( 1, -1,  1,  0, -1,  0,  0,  0, -1, -2, -2,  0, -1, -2, -1,  4,  1, -3, -2, -2),
    /* T */ Array( 0, -1,  0, -1, -1, -1, -1, -2, -2, -1, -1, -1, -1, -2, -1,  1,  5, -2, -2,  0),
    /* W */ Array(-3, -3, -4, -4, -2, -2, -3, -2, -2, -3, -2, -3, -1,  1, -4, -3, -2, 11,  2, -3),
    /* Y */ Array(-2, -2, -2, -3, -2, -1, -2, -3,  2, -1, -1, -2, -1,  3, -3, -2, -2,  2,  7, -1),
    /* V */ Array( 0, -3, -3, -3, -1, -2, -2, -3, -3,  3,  1, -2,  1, -1, -2, -2,  0, -3, -1,  4)
  )

  /** BLOSUM62 substitution score for the amino-acid pair `(a, b)`.
    *
    * Symmetric: `score(a, b) == score(b, a)` for every pair.
    */
  def score(a: AminoAcid, b: AminoAcid): Int =
    matrix(ordinal(a))(ordinal(b))
}
