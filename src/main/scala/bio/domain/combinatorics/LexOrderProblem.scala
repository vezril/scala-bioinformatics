package bio.domain.combinatorics

/** Validated input bundle for the Rosalind LEXV ("Ordering Strings of Varying
  * Length Lexicographically") problem — see
  * [[bio.algorithms.combinatorics.VaryingLengthLexOrder.enumerate]].
  *
  * Pairs an ordered `alphabet` (retained in the *given* order, which defines the
  * lexicographic order) with the maximum word length `maxLength`. The smart
  * constructor validates, first-failure-wins:
  *   1. `alphabet.nonEmpty`, else `EmptyAlphabet`;
  *   2. `alphabet.size <= 12` (Rosalind cap), else `TooManySymbols`;
  *   3. no symbol repeats, else `DuplicateSymbol`;
  *   4. `maxLength >= 1`, else `NonPositiveLength`;
  *   5. `maxLength <= 4` (Rosalind cap), else `LengthExceedsMaximum`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[LexOrderProblem.from]].
  */
sealed abstract case class LexOrderProblem(alphabet: Vector[Char], maxLength: Int)

object LexOrderProblem {
  private val MaxSymbols: Int = 12
  private val MaxLength: Int   = 4

  def from(
      alphabet: Vector[Char],
      maxLength: Int
  ): Either[LexOrderProblemError, LexOrderProblem] =
    if (alphabet.isEmpty)
      Left(LexOrderProblemError.EmptyAlphabet)
    else if (alphabet.size > MaxSymbols)
      Left(LexOrderProblemError.TooManySymbols(alphabet.size, MaxSymbols))
    else
      firstDuplicate(alphabet)
        .map(LexOrderProblemError.DuplicateSymbol)
        .toLeft(())
        .flatMap { _ =>
          if (maxLength < 1) Left(LexOrderProblemError.NonPositiveLength(maxLength))
          else if (maxLength > MaxLength)
            Left(LexOrderProblemError.LengthExceedsMaximum(maxLength, MaxLength))
          else Right(new LexOrderProblem(alphabet, maxLength) {})
        }

  /** The first symbol that appears more than once, scanning left to right. */
  private def firstDuplicate(alphabet: Vector[Char]): Option[Char] =
    alphabet.iterator
      .scanLeft((Set.empty[Char], Option.empty[Char])) { case ((seen, _), ch) =>
        if (seen.contains(ch)) (seen, Some(ch)) else (seen + ch, None)
      }
      .collectFirst { case (_, Some(dup)) => dup }
}
