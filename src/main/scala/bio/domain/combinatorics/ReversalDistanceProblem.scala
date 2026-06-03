package bio.domain.combinatorics

/** Validated input for the Rosalind REAR ("Reversal Distance") problem — see
  * [[bio.algorithms.combinatorics.ReversalDistanceSearch.distance]].
  *
  * Wraps a `source` and a `target` [[Permutation]]. The smart constructor
  * enforces, with first-failure-wins ordering: the two permutations have equal
  * length, then that length is ≤ 10 (the cap that keeps the bidirectional BFS
  * tractable). Two equal empty permutations are accepted (distance 0).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[ReversalDistanceProblem.from]].
  */
sealed abstract case class ReversalDistanceProblem(source: Permutation, target: Permutation)

object ReversalDistanceProblem {
  private val MaxLength: Int = 10

  def from(
      source: Permutation,
      target: Permutation
  ): Either[ReversalDistanceProblemError, ReversalDistanceProblem] = {
    val sLen = source.values.length
    val tLen = target.values.length
    if (sLen != tLen)
      Left(ReversalDistanceProblemError.LengthMismatch(sLen, tLen))
    else if (sLen > MaxLength)
      Left(ReversalDistanceProblemError.LengthExceedsMax(sLen, MaxLength))
    else
      Right(new ReversalDistanceProblem(source, target) {})
  }
}
