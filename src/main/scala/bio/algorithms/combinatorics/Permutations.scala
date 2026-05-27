package bio.algorithms.combinatorics

import bio.domain.combinatorics.PermutationLength

/** Enumerates every permutation of `{1, 2, ..., length.value}`.
  *
  * Uses Scala's stdlib `Iterable.permutations`, which yields permutations in
  * lexicographic order. The result contains exactly `length.value!` permutations
  * (e.g., `length = 3` → 6, `length = 7` → 5040). Each output permutation is a
  * `Vector[Int]` of size `length.value` containing every value in `1..length.value`
  * exactly once.
  */
object Permutations {

  def enumerate(length: PermutationLength): Vector[Vector[Int]] =
    (1 to length.value).toVector.permutations.toVector
}
