package bio.domain.genetics

sealed trait IndependentAllelesProblemError
object IndependentAllelesProblemError {
  final case class NonPositiveGenerations(value: Int) extends IndependentAllelesProblemError
  final case class NonPositiveAtLeast(value: Int)     extends IndependentAllelesProblemError
  final case class AtLeastExceedsPopulation(atLeast: Int, generations: Int)
      extends IndependentAllelesProblemError
}
