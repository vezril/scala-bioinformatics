package bio.domain.recurrence

sealed trait MortalRabbitProblemError
object MortalRabbitProblemError {
  final case class NonPositiveMonths(value: Int)   extends MortalRabbitProblemError
  final case class NonPositiveLifespan(value: Int) extends MortalRabbitProblemError
}
