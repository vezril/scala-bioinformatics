package bio.domain.graph

/** Construction failures for [[OverlapLength]]. */
sealed trait OverlapLengthError
object OverlapLengthError {

  /** The requested overlap length was less than 1. Carries the offending value. */
  final case class NonPositive(value: Int) extends OverlapLengthError
}
