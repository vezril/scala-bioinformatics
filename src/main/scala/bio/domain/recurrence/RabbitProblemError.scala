package bio.domain.recurrence

sealed trait RabbitProblemError
object RabbitProblemError {
  final case class NonPositiveMonths(value: Int) extends RabbitProblemError
  final case class NegativeLitterSize(value: Int) extends RabbitProblemError
}
