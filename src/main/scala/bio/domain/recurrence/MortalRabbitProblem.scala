package bio.domain.recurrence

/** Parameters for the mortal-Fibonacci-rabbits recurrence.
  *
  * Constructable only via [[MortalRabbitProblem.from]] which enforces:
  *   - `months >= 1` (the recurrence is defined starting at month 1)
  *   - `lifespan >= 1` (a pair lives at least one month — the month it was born)
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor. Distinct from the spec-6
  * [[RabbitProblem]] because the recurrence parameters differ semantically
  * (`litterSize` vs `lifespan`).
  */
sealed abstract case class MortalRabbitProblem(months: Int, lifespan: Int)

object MortalRabbitProblem {
  def from(months: Int, lifespan: Int): Either[MortalRabbitProblemError, MortalRabbitProblem] =
    if (months < 1)
      Left(MortalRabbitProblemError.NonPositiveMonths(months))
    else if (lifespan < 1)
      Left(MortalRabbitProblemError.NonPositiveLifespan(lifespan))
    else
      Right(new MortalRabbitProblem(months, lifespan) {})
}
