package bio.domain.protein

sealed trait ProteinError
object ProteinError {
  final case class InvalidCharacter(ch: Char) extends ProteinError
}
