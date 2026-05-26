package bio.domain.genetics

sealed trait CouplePopulationError
object CouplePopulationError {
  final case class NegativeCount(index: Int, value: Int)   extends CouplePopulationError
  final case class ExceedsMaxCount(index: Int, value: Int) extends CouplePopulationError
}
