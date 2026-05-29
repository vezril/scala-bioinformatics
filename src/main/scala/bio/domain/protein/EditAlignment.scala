package bio.domain.protein

/** Result of an optimal Levenshtein alignment between two protein strings —
  * see [[bio.algorithms.protein.EditDistanceAlignment.align]].
  *
  * Carries the integer Levenshtein distance plus the two *augmented* strings
  * `augmentedLeft` and `augmentedRight` that realise it. Each augmented
  * string is the original input with `-` gap symbols inserted at the
  * positions where the alignment introduces an insertion or deletion.
  *
  * Because `-` is not a valid amino-acid code, the augmented strings are
  * plain `String`, *not* [[ProteinString]]. The original inputs (without
  * gaps) remain available on the producing [[EditDistanceAlignmentProblem]].
  *
  * Invariants enforced by the producing algorithm (not by this ADT):
  *
  *   - `augmentedLeft.length == augmentedRight.length`;
  *   - no column has gap symbols in both rows;
  *   - stripping `-` from `augmentedLeft` recovers the original left input;
  *     stripping `-` from `augmentedRight` recovers the original right input;
  *   - the Hamming distance between `augmentedLeft` and `augmentedRight`
  *     equals `distance`.
  *
  * Implemented as a plain `final case class` (free `apply`, `copy`,
  * equality, pattern-matching) because it is a result value with no
  * cross-field invariant beyond what the producing algorithm guarantees.
  */
final case class EditAlignment(
    distance: Int,
    augmentedLeft: String,
    augmentedRight: String
)
