package bio.domain.analysis

sealed trait GcContentError
object GcContentError {
  final case class OutOfRange(value: Double) extends GcContentError
  case object NotFinite                       extends GcContentError
}
