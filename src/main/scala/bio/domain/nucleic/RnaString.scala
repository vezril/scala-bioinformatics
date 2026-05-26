package bio.domain.nucleic

final class RnaString private (val value: String) extends AnyVal

object RnaString {
  private val MaxLength = 10000

  def from(s: String): Either[SequenceError, RnaString] =
    if (s.length > MaxLength)
      Left(SequenceError.ExceedsMaxLength(s.length))
    else
      s.find(!RnaNucleotide.validChars.contains(_)) match {
        case Some(ch) => Left(SequenceError.InvalidCharacter(ch))
        case None     => Right(new RnaString(s))
      }

  /** Internal constructor for trusted callers that guarantee `s` contains only A, C, G, U.
    * Bypasses validation. Use [[from]] for any externally-sourced input.
    */
  private[bio] def unsafeFrom(s: String): RnaString = new RnaString(s)
}
