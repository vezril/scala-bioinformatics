package bio.domain.combinatorics

/** Validation errors for [[ReversalDistanceProblem]] (Rosalind REAR). */
sealed trait ReversalDistanceProblemError

object ReversalDistanceProblemError {

  /** The two permutations have different lengths. */
  final case class LengthMismatch(sourceLength: Int, targetLength: Int)
      extends ReversalDistanceProblemError

  /** The permutation length exceeds the BFS-tractable maximum. */
  final case class LengthExceedsMax(length: Int, max: Int) extends ReversalDistanceProblemError
}
