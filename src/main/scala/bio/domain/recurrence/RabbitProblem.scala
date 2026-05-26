package bio.domain.recurrence

/** Parameters for the generalized Fibonacci-rabbits recurrence.
  * Constructable only via [[RabbitProblem.from]] which enforces:
  *   - `months >= 1` (the recurrence is defined starting at F(1))
  *   - `litterSize >= 0` (negative litters are biologically meaningless)
  *
  * Implemented as `sealed abstract case class` to prevent Scala 2.13's case-class
  * `apply` and `copy` synthesis from leaking around the smart constructor.
  */
sealed abstract case class RabbitProblem(months: Int, litterSize: Int)

object RabbitProblem {
  def from(months: Int, litterSize: Int): Either[RabbitProblemError, RabbitProblem] =
    if (months < 1)
      Left(RabbitProblemError.NonPositiveMonths(months))
    else if (litterSize < 0)
      Left(RabbitProblemError.NegativeLitterSize(litterSize))
    else
      Right(new RabbitProblem(months, litterSize) {})
}
