package bio.domain.nucleic

sealed trait SequenceError
object SequenceError {
  final case class InvalidCharacter(ch: Char)   extends SequenceError
  final case class ExceedsMaxLength(length: Int) extends SequenceError
}
