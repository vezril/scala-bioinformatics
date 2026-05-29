package bio.domain.protein

/** Result of a Smith-Waterman local alignment — see
  * [[bio.algorithms.protein.LocalAlignment.compute]].
  *
  * Carries the maximum local-alignment score plus the two *substrings* of
  * the original inputs that achieved it.
  *
  *   - `leftSubstring` is a (possibly empty) contiguous region of
  *     `problem.left.value`;
  *   - `rightSubstring` is a (possibly empty) contiguous region of
  *     `problem.right.value`.
  *
  * Both substrings are plain `String`s without `-` gap characters — they
  * are the original contiguous regions of the inputs, not augmented
  * alignment strings. The aligned form (with gaps) can be reconstructed by
  * running Needleman-Wunsch on the pair, but LOCA does not surface it.
  *
  * Implemented as a plain `final case class` (free `apply`, `copy`,
  * equality, pattern-matching) because it is a result value with no
  * cross-field invariant beyond what the producing algorithm guarantees.
  */
final case class LocalAlignment(
    score: Int,
    leftSubstring: String,
    rightSubstring: String
)
