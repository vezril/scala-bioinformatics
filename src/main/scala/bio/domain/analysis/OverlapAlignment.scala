package bio.domain.analysis

/** Result of the Rosalind OAP ("Overlap Alignment") problem — one optimal
  * overlap alignment of a suffix of `s` against a prefix of `t`.
  *
  *   - `score` is the optimal overlap-alignment score (match `+1`, substitution
  *     `-2`, gap `-2`); it is never negative (the empty overlap scores 0).
  *   - `augmentedS` is the aligned suffix of `s`, with `-` for gaps.
  *   - `augmentedT` is the aligned prefix of `t`, with `-` for gaps.
  *
  * The augmented strings are plain `String` (they contain `-`, not a valid DNA
  * base) and have equal length.
  */
final case class OverlapAlignment(
    score: Int,
    augmentedS: String,
    augmentedT: String
) {

  /** Rosalind OAP rendering: score, augmented `s`, augmented `t`, one per line. */
  def format: String = s"$score\n$augmentedS\n$augmentedT"
}
