package bio.domain.analysis

/** Result of an optimal *fitting alignment* between a substring of a text
  * string `s` and all of a motif `t` (Rosalind SIMS — "Finding a Motif
  * with Modifications"). See
  * [[bio.algorithms.analysis.FittingAlignment.align]].
  *
  * Carries the maximum *mismatch score* (`+1` per matched symbol, `-1` per
  * mismatched, inserted, or deleted symbol) plus the two *augmented*
  * strings that realise it:
  *
  *   - `augmentedText` is a contiguous substring of the original text with
  *     `-` gap symbols inserted;
  *   - `augmentedMotif` is the *entire* original motif with `-` gap symbols
  *     inserted (fitting alignment consumes all of the motif).
  *
  * Because `-` is not a valid DNA base, the augmented strings are plain
  * `String`, not `DnaString`.
  *
  * Invariants enforced by the producing algorithm (not by this ADT):
  *
  *   - `augmentedText.length == augmentedMotif.length`;
  *   - no column has gap symbols in both rows;
  *   - stripping `-` from `augmentedMotif` recovers the entire motif input;
  *   - stripping `-` from `augmentedText` is a contiguous substring of the
  *     text input;
  *   - the mismatch score of the aligned pair equals `score`.
  *
  * Note: unlike a local (Smith-Waterman) alignment, a fitting alignment
  * must pay for every motif symbol, so `score` MAY be negative.
  *
  * Implemented as a plain `final case class` (free `apply`, `copy`,
  * equality, pattern-matching) because it is a result value with no
  * cross-field invariant beyond what the producing algorithm guarantees.
  */
final case class FittingAlignment(
    score: Int,
    augmentedText: String,
    augmentedMotif: String
)
