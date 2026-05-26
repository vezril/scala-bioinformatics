package bio.domain.nucleic

final class DnaString private (val value: String) extends AnyVal

object DnaString {
  private val MaxLength = 1000

  def from(s: String): Either[SequenceError, DnaString] =
    if (s.length > MaxLength)
      Left(SequenceError.ExceedsMaxLength(s.length))
    else
      s.find(!DnaNucleotide.validChars.contains(_)) match {
        case Some(ch) => Left(SequenceError.InvalidCharacter(ch))
        case None     => Right(new DnaString(s))
      }

  /** Internal constructor for trusted callers that guarantee `s` contains only A, C, G, T.
    * Bypasses validation. Use [[from]] for any externally-sourced input.
    */
  private[bio] def unsafeFrom(s: String): DnaString = new DnaString(s)
}
