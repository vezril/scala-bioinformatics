package bio.parsing

import bio.domain.nucleic.SequenceError

sealed trait FastaParseError
object FastaParseError {
  case object MissingHeader extends FastaParseError
  case object EmptyId       extends FastaParseError
  final case class InvalidDna(id: String, cause: SequenceError) extends FastaParseError
}
