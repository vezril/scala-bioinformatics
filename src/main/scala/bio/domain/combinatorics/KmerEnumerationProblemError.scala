package bio.domain.combinatorics

/** Construction failures for [[KmerEnumerationProblem]]. */
sealed trait KmerEnumerationProblemError
object KmerEnumerationProblemError {

  /** The alphabet was empty — at least one symbol is required. */
  case object EmptyAlphabet extends KmerEnumerationProblemError

  /** The alphabet exceeded the Rosalind LEXF cap of 10 symbols. */
  final case class TooManySymbols(count: Int, max: Int) extends KmerEnumerationProblemError

  /** The alphabet contained the same symbol more than once. */
  final case class DuplicateSymbol(symbol: Char) extends KmerEnumerationProblemError

  /** The requested word length was not positive. */
  final case class NonPositiveLength(length: Int) extends KmerEnumerationProblemError

  /** The requested word length exceeded the Rosalind LEXF cap of 10. */
  final case class LengthExceedsMaximum(length: Int, max: Int)
      extends KmerEnumerationProblemError
}
