package bio.domain.analysis

/** Result of an optimal 4-string multiple alignment under the linear
  * `match = 0` / `mismatch = -1` scoring scheme — see
  * [[bio.algorithms.analysis.MultipleAlignment.align]].
  *
  * Carries the maximum alignment score plus four *augmented strings* — the
  * original DNA inputs with `-` gap symbols inserted at the positions where
  * the alignment introduces an insertion or deletion for that row.
  *
  * Because `-` is not a valid DNA character, the augmented strings are plain
  * `String`, *not* `DnaString`. The original inputs (without gaps) remain
  * available on the producing
  * [[bio.domain.analysis.MultipleAlignmentProblem]].
  *
  * Invariants enforced by the producing algorithm (not by this ADT):
  *
  *   - `augmentedStrings.size == 4`;
  *   - all four augmented strings have equal length;
  *   - no column has gap symbols in *every* row;
  *   - stripping `-` from `augmentedStrings(k)` recovers the original
  *     `problem.strings(k).value` for every `k` in `0..3`;
  *   - the score equals the sum of `-1` per mismatch position over all
  *     `C(4, 2) = 6` unordered pairs of rows.
  *
  * Implemented as a plain `final case class` (free `apply`, `copy`,
  * equality, pattern-matching) because it is a result value with no
  * cross-field invariant beyond what the producing algorithm guarantees.
  */
final case class MultipleAlignment(
    score: Int,
    augmentedStrings: Vector[String]
)
