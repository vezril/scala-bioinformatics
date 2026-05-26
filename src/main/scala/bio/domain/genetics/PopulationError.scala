package bio.domain.genetics

sealed trait PopulationError
object PopulationError {
  case object NegativeCount           extends PopulationError
  case object InsufficientPopulation  extends PopulationError
}
