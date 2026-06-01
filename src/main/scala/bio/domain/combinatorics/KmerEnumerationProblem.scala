package bio.domain.combinatorics

/** Validated input bundle for the Rosalind LEXF ("Enumerating k-mers
  * Lexicographically") problem — see
  * [[bio.algorithms.combinatorics.EnumerateKmers.enumerate]].
  *
  * Pairs an ordered `alphabet` (retained in the *given* order, which defines the
  * lexicographic order) with the word `length`. The smart constructor validates,
  * first-failure-wins, in the order:
  *   1. `alphabet.nonEmpty`, else `EmptyAlphabet`;
  *   2. `alphabet.size <= 10` (Rosalind cap), else `TooManySymbols`;
  *   3. no symbol repeats, else `DuplicateSymbol`;
  *   4. `length >= 1`, else `NonPositiveLength`;
  *   5. `length <= 10` (Rosalind cap), else `LengthExceedsMaximum`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[KmerEnumerationProblem.from]].
  */
sealed abstract case class KmerEnumerationProblem(alphabet: Vector[Char], length: Int)

object KmerEnumerationProblem {
  private val MaxSymbols: Int = 10
  private val MaxLength: Int  = 10

  def from(
      alphabet: Vector[Char],
      length: Int
  ): Either[KmerEnumerationProblemError, KmerEnumerationProblem] =
    if (alphabet.isEmpty)
      Left(KmerEnumerationProblemError.EmptyAlphabet)
    else if (alphabet.size > MaxSymbols)
      Left(KmerEnumerationProblemError.TooManySymbols(alphabet.size, MaxSymbols))
    else
      firstDuplicate(alphabet)
        .map(KmerEnumerationProblemError.DuplicateSymbol)
        .toLeft(())
        .flatMap { _ =>
          if (length < 1) Left(KmerEnumerationProblemError.NonPositiveLength(length))
          else if (length > MaxLength)
            Left(KmerEnumerationProblemError.LengthExceedsMaximum(length, MaxLength))
          else Right(new KmerEnumerationProblem(alphabet, length) {})
        }

  /** The first symbol that appears more than once, scanning left to right. */
  private def firstDuplicate(alphabet: Vector[Char]): Option[Char] =
    alphabet.iterator
      .scanLeft((Set.empty[Char], Option.empty[Char])) { case ((seen, _), ch) =>
        if (seen.contains(ch)) (seen, Some(ch)) else (seen + ch, None)
      }
      .collectFirst { case (_, Some(dup)) => dup }
}
