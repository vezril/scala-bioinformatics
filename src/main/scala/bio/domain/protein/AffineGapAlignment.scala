package bio.domain.protein

/** Result of an optimal *affine-gap* global alignment between two protein
  * strings — see [[bio.algorithms.protein.AffineGapAlignment.compute]]
  * (Rosalind GAFF, "Global Alignment with Scoring Matrix and Affine Gap
  * Penalty").
  *
  * Carries the maximum global-alignment `score` under BLOSUM62 with an affine
  * gap penalty (`a + b·(L − 1)`, `a = 11`, `b = 1`) plus the two *augmented*
  * strings `augmentedLeft` and `augmentedRight` that realise one optimal
  * alignment. Each augmented string is the original input with `-` gap
  * symbols inserted at the alignment's insertion/deletion positions.
  *
  * Because `-` is not a valid amino-acid code, the augmented strings are
  * plain `String`, *not* [[ProteinString]]. The original inputs (without
  * gaps) remain available on the producing [[AffineGapAlignmentProblem]].
  *
  * Invariants enforced by the producing algorithm (not by this ADT):
  *
  *   - `augmentedLeft.length == augmentedRight.length`;
  *   - no column has gap symbols in both rows;
  *   - stripping `-` from `augmentedLeft` recovers the original left input;
  *     stripping `-` from `augmentedRight` recovers the original right input;
  *   - the affine-scored value of the alignment equals `score`.
  *
  * Implemented as a plain `final case class` (free `apply`, `copy`,
  * equality, pattern-matching) because it is a result value with no
  * cross-field invariant beyond what the producing algorithm guarantees.
  */
final case class AffineGapAlignment(
    score: Int,
    augmentedLeft: String,
    augmentedRight: String
)
