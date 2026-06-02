package bio.algorithms.combinatorics

import bio.domain.combinatorics.RootedTreeLeafCount

/** Counts the number of distinct rooted binary trees on `n` labeled taxa modulo
  * `1,000,000` (Rosalind ROOT).
  *
  * **Math:** `B(n) = (2n − 3)!! = 1 · 3 · 5 · ... · (2n − 3)` for `n ≥ 2`, and `1` for
  * `n = 1`. Adding the `k`-th taxon to a rooted tree on `k − 1` taxa can attach to any
  * of its `2(k − 1) − 1 = 2k − 3` edges, so the count multiplies by the successive odd
  * numbers `3, 5, ..., (2n − 3)`. This is the rooted counterpart of CUNR's
  * `(2n − 5)!!` (see [[UnrootedBinaryTrees]]). Rosalind asks for `B(n) mod 1,000,000`.
  *
  * **Implementation:** per-step modulo with `Int` arithmetic over the odd factors, the
  * same idiom as [[UnrootedBinaryTrees.count]]:
  *
  * {{{
  *   (3 to (2 * leaves.value - 3) by 2).foldLeft(1) { (acc, k) => (acc * k) % Modulus }
  * }}}
  *
  * For `n ≤ 2` the range `3 to (2n − 3) by 2` is empty (upper bound `< 3`), so the fold
  * returns the initial `1` — the correct count, with the leading `1` of the double
  * factorial folded in implicitly.
  *
  * **`Int` safety:** after each step `acc ∈ [0, 999_999]`. The largest factor is
  * `2 · 1000 − 3 = 1997`, so the worst intermediate is `999_999 × 1997 = 1_996_998_003`,
  * within `Int.MaxValue ≈ 2.15 × 10^9`. No overflow.
  */
object RootedBinaryTrees {

  private val Modulus: Int = 1_000_000

  def count(leaves: RootedTreeLeafCount): Int =
    (3 to (2 * leaves.value - 3) by 2).foldLeft(1) { (acc, k) =>
      (acc * k) % Modulus
    }
}
