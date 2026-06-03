package bio.domain.combinatorics

/** Construction failures for [[LexOrderProblem]] (Rosalind LEXV). */
sealed trait LexOrderProblemError
object LexOrderProblemError {

  /** The alphabet was empty — at least one symbol is required. */
  case object EmptyAlphabet extends LexOrderProblemError

  /** The alphabet exceeded the Rosalind LEXV cap of 12 symbols. */
  final case class TooManySymbols(count: Int, max: Int) extends LexOrderProblemError

  /** The alphabet contained the same symbol more than once. */
  final case class DuplicateSymbol(symbol: Char) extends LexOrderProblemError

  /** The requested maximum length was not positive. */
  final case class NonPositiveLength(length: Int) extends LexOrderProblemError

  /** The requested maximum length exceeded the Rosalind LEXV cap of 4. */
  final case class LengthExceedsMaximum(length: Int, max: Int) extends LexOrderProblemError
}
