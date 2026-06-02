package bio.domain.graph

import bio.domain.nucleic.DnaString

/** Validated input for the Rosalind TRIE ("Introduction to Pattern Matching")
  * problem — see [[bio.algorithms.graph.TrieConstruction.construct]].
  *
  * Wraps the collection of pattern `DnaString`s from which the trie is built.
  * DNA-character validity is owned upstream by `DnaString`, so the smart
  * constructor validates the collection shape and the prefix-freedom invariant,
  * first failure wins:
  *   1. `patterns.size <= 100`, else `TooManyPatterns`;
  *   2. each pattern `length <= 100` (per-pattern cap, index order), else `PatternTooLong`;
  *   3. no pattern is a prefix of another, else `PrefixConflict`.
  *
  * The empty collection is accepted (it yields a root-only trie with no edges).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[PatternTrieProblem.from]].
  */
sealed abstract case class PatternTrieProblem(patterns: Vector[DnaString])

object PatternTrieProblem {
  private val MaxPatterns: Int      = 100
  private val MaxPatternLength: Int = 100

  def from(
      patterns: Vector[DnaString]
  ): Either[PatternTrieProblemError, PatternTrieProblem] =
    if (patterns.size > MaxPatterns)
      Left(PatternTrieProblemError.TooManyPatterns(patterns.size, MaxPatterns))
    else
      firstTooLong(patterns)
        .orElse(firstPrefixConflict(patterns))
        .toLeft(new PatternTrieProblem(patterns) {})

  /** The first pattern (index order) whose length exceeds the cap. */
  private def firstTooLong(patterns: Vector[DnaString]): Option[PatternTrieProblemError] =
    patterns.iterator.zipWithIndex.collectFirst {
      case (p, i) if p.value.length > MaxPatternLength =>
        PatternTrieProblemError.PatternTooLong(i, p.value.length, MaxPatternLength)
    }

  /** The first ordered pair `(i, j)`, `i != j`, where pattern `i` is a prefix of
    * (or equal to) pattern `j`.
    */
  private def firstPrefixConflict(
      patterns: Vector[DnaString]
  ): Option[PatternTrieProblemError] = {
    val indexed = patterns.zipWithIndex
    indexed.iterator
      .flatMap { case (a, i) =>
        indexed.iterator.collect {
          case (b, j) if i != j && b.value.startsWith(a.value) =>
            PatternTrieProblemError.PrefixConflict(i, j)
        }
      }
      .nextOption()
  }
}
