package bio.domain.analysis

/** Result of the Rosalind SMGB ("Semiglobal Alignment") problem — one optimal
  * semiglobal alignment of all of `s` against all of `t`, with leading/trailing
  * gap runs on either string free.
  *
  *   - `score` is the optimal semiglobal-alignment score (match `+1`,
  *     substitution `-1`, scored gap `-1`; leading/trailing gaps are free).
  *   - `augmentedS` is the aligned `s`, with `-` for gaps.
  *   - `augmentedT` is the aligned `t`, with `-` for gaps.
  *
  * The augmented strings are plain `String` (they contain `-`, not a valid DNA
  * base) and have equal length. Removing the gap symbols from `augmentedS`
  * reproduces `s`, and likewise for `augmentedT` and `t`.
  */
final case class SemiglobalAlignment(
    score: Int,
    augmentedS: String,
    augmentedT: String
) {

  /** Rosalind SMGB rendering: score, augmented `s`, augmented `t`, one per line. */
  def format: String = s"$score\n$augmentedS\n$augmentedT"
}
