package bio.domain.graph

/** Validation errors for [[PatternTrieProblem]] (Rosalind TRIE). */
sealed trait PatternTrieProblemError

object PatternTrieProblemError {

  /** The pattern collection exceeds the maximum allowed size. */
  final case class TooManyPatterns(size: Int, max: Int) extends PatternTrieProblemError

  /** The pattern at `index` exceeds the maximum allowed length. */
  final case class PatternTooLong(index: Int, length: Int, max: Int)
      extends PatternTrieProblemError

  /** The pattern at `prefixIndex` is a prefix of (or equal to) the pattern at
    * `ofIndex`, violating the requirement that no pattern is a prefix of another.
    */
  final case class PrefixConflict(prefixIndex: Int, ofIndex: Int)
      extends PatternTrieProblemError
}
