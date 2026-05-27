package bio.parsing

/** Parse failures for [[NewickParser]]. Each case carries enough context to point at
  * the offending location in the input.
  */
sealed trait NewickParseError
object NewickParseError {

  /** The input was empty (after trimming surrounding whitespace). */
  case object EmptyInput extends NewickParseError

  /** The input did not end with the required terminating `;`. */
  case object MissingTerminator extends NewickParseError

  /** An open paren had no matching close paren. The position points at the offending `(`. */
  final case class UnmatchedOpenParen(position: Int) extends NewickParseError

  /** A close paren had no matching open paren. The position points at the offending `)`. */
  final case class UnmatchedCloseParen(position: Int) extends NewickParseError

  /** A character outside the Newick grammar appeared at the given position. */
  final case class UnexpectedCharacter(char: Char, position: Int) extends NewickParseError

  /** Content remained after the terminating `;`. The remaining (trimmed) suffix is captured. */
  final case class TrailingContent(remaining: String) extends NewickParseError
}
