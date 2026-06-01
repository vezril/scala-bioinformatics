package bio.algorithms.combinatorics

import bio.domain.combinatorics.LeafCount

/** Counts the number of distinct unrooted binary trees on `n` labeled leaves
  * modulo `1,000,000` (Rosalind CUNR).
  *
  * **Math:** `b(n) = (2n − 5)!! = 1 · 3 · 5 · ... · (2n − 5)` for `n ≥ 3`, and
  * `1` for `n ≤ 2`. Adding the `k`-th leaf to a tree on `k − 1` leaves can
  * attach to any of its `2(k − 1) − 3 = 2k − 5` edges, so the count multiplies
  * by the successive odd numbers `3, 5, ..., (2n − 5)`. The Rosalind problem
  * asks for `b(n) mod 1,000,000` because `b(1000)` has hundreds of digits.
  *
  * **Implementation:** per-step modulo with `Int` arithmetic over the odd
  * factors, the same idiom used by [[Subsets.count]]:
  *
  * {{{
  *   (3 to (2 * leaves.value - 5) by 2).foldLeft(1) { (acc, k) => (acc * k) % Modulus }
  * }}}
  *
  * For `n ≤ 3` the factor range `3 to (2n − 5) by 2` is empty (its upper bound
  * is `< 3`), so the fold returns the initial `1` — the correct count of a
  * single tree, with the leading `1` of the double factorial folded in
  * implicitly.
  *
  * **`Int` safety:** after each step `acc ∈ [0, 999_999]`. The largest factor
  * is `2 · 1000 − 5 = 1995`, so the worst intermediate is
  * `999_999 × 1995 = 1_994_998_005`, within `Int.MaxValue ≈ 2.15 × 10^9`. No
  * overflow.
  *
  * **Complexity:** O(n) integer multiplies (≤ ~500 odd factors at `n ≤ 1000`)
  * — trivially fast.
  */
object UnrootedBinaryTrees {

  private val Modulus: Int = 1_000_000

  def count(leaves: LeafCount): Int =
    (3 to (2 * leaves.value - 5) by 2).foldLeft(1) { (acc, k) =>
      (acc * k) % Modulus
    }
}
