package bio.domain.protein

/** Result of a local alignment under an affine gap penalty — see
  * [[bio.algorithms.protein.LocalAffineAlignment.compute]].
  *
  * Carries the maximum local-alignment score plus the two *substrings* of the
  * original inputs that achieved it:
  *
  *   - `leftSubstring` is a (possibly empty) contiguous region of
  *     `problem.left.value`;
  *   - `rightSubstring` is a (possibly empty) contiguous region of
  *     `problem.right.value`.
  *
  * Both substrings are plain `String`s without `-` gap characters — they are
  * the original contiguous regions of the inputs, not augmented alignment
  * strings.
  *
  * Implemented as a plain `final case class` (free `apply`, `copy`, equality,
  * pattern-matching) because it is a result value with no cross-field
  * invariant beyond what the producing algorithm guarantees.
  */
final case class LocalAffineAlignment(
    score: Int,
    leftSubstring: String,
    rightSubstring: String
)
