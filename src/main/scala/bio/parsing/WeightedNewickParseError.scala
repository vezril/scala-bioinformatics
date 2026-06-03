package bio.parsing

/** Parse failures for [[WeightedNewickParser]] (Rosalind NKEW). */
sealed trait WeightedNewickParseError
object WeightedNewickParseError {
  case object EmptyInput extends WeightedNewickParseError
  case object MissingTerminator extends WeightedNewickParseError
  final case class UnmatchedOpenParen(position: Int) extends WeightedNewickParseError
  final case class UnmatchedCloseParen(position: Int) extends WeightedNewickParseError
  final case class UnexpectedCharacter(char: Char, position: Int) extends WeightedNewickParseError
  final case class TrailingContent(remaining: String) extends WeightedNewickParseError
  final case class InvalidWeight(text: String, position: Int) extends WeightedNewickParseError
}
