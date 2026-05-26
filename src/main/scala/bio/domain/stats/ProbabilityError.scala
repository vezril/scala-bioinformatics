package bio.domain.stats

sealed trait ProbabilityError
object ProbabilityError {
  final case class OutOfRange(value: Double) extends ProbabilityError
  case object NotFinite                       extends ProbabilityError
}
