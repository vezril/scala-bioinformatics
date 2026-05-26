package bio.domain.analysis

sealed trait HammingError
object HammingError {
  final case class LengthMismatch(lengthA: Int, lengthB: Int) extends HammingError
}
