package bio.domain.combinatorics

/** Validation errors for [[RestrictionMapProblem]] (Rosalind PDPL). */
sealed trait RestrictionMapProblemError

object RestrictionMapProblemError {

  /** The multiset size is not a triangular number `n(n-1)/2` for any positive `n`. */
  final case class InvalidSize(size: Int) extends RestrictionMapProblemError

  /** The distance at `index` is not a positive integer. */
  final case class NonPositiveDistance(index: Int, value: Int)
      extends RestrictionMapProblemError
}
