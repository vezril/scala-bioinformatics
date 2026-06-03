package bio.domain.combinatorics

/** A validated permutation of `{1, …, n}` where `n` is its length — the input
  * domain for the Rosalind LGIS ("Longest Increasing Subsequence") problem (see
  * [[bio.algorithms.combinatorics.LongestSubsequences.find]]).
  *
  * The smart constructor enforces, with first-failure-wins ordering: length
  * `≤ 10000`, then that the values are exactly a permutation of `{1, …, length}`
  * (their sorted form equals `1 to length`). The empty vector is accepted (the
  * empty permutation of `{1..0}`).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[Permutation.from]].
  */
sealed abstract case class Permutation(values: Vector[Int])

object Permutation {
  private val MaxLength: Int = 10000

  def from(values: Vector[Int]): Either[PermutationError, Permutation] =
    if (values.length > MaxLength)
      Left(PermutationError.TooLong(values.length, MaxLength))
    else if (values.sorted != Vector.range(1, values.length + 1))
      Left(PermutationError.NotAPermutation(values))
    else
      Right(new Permutation(values) {})
}
