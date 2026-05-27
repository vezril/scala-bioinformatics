package bio.algorithms.combinatorics

import bio.domain.combinatorics.SubsetUniverseSize

/** Counts the total number of subsets of `{1, 2, ..., n}` modulo `1,000,000`
  * (Rosalind SSET).
  *
  * **Math:** a set of `n` elements has exactly `2^n` subsets (each element is
  * independently included or excluded — `n` binary "on/off" switches). The Rosalind
  * problem asks for `2^n mod 1,000,000` because `2^1000` has ~301 decimal digits.
  *
  * **Implementation:** per-step modulo with `Int` arithmetic. The same idiom used by
  * [[PartialPermutations.count]]:
  *
  * {{{
  *   (0 until size.value).foldLeft(1) { (acc, _) => (acc * 2) % Modulus }
  * }}}
  *
  * **`Int` safety:** after each step `acc ∈ [0, 999_999]`. The worst intermediate is
  * `999_999 × 2 = 1_999_998`, well within `Int.MaxValue ≈ 2.15 × 10^9`. No overflow.
  *
  * **Complexity:** O(n) integer multiplies. At `n ≤ 1000` that's at most 1000
  * multiplications — trivially fast. A `O(log n)` square-and-multiply variant would
  * be faster asymptotically but is unnecessary at this scale and would deviate from
  * the existing per-step-modulo idiom.
  */
object Subsets {

  private val Modulus: Int = 1_000_000

  def count(size: SubsetUniverseSize): Int =
    (0 until size.value).foldLeft(1) { (acc, _) => (acc * 2) % Modulus }
}
