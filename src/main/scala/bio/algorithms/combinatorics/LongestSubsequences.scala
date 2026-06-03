package bio.algorithms.combinatorics

import bio.domain.combinatorics.{MonotonicSubsequences, Permutation}

import scala.collection.mutable.ArrayBuffer

/** Computes a longest increasing subsequence and a longest decreasing
  * subsequence of a permutation for Rosalind LGIS ("Longest Increasing
  * Subsequence").
  *
  * **Algorithm.** Patience sorting in O(n log n) with predecessor
  * reconstruction. `tails(l)` holds the index (into the value vector) of the
  * smallest possible tail of a monotone subsequence of length `l+1` found so
  * far. For each position `i`, a binary search finds the first slot whose value
  * is not "before" `v(i)` under the supplied comparison; `i` is written there
  * and `pred(i)` records the index in the preceding slot. Walking `pred` back
  * from the last filled slot and reversing reconstructs the subsequence.
  *
  * A longest *decreasing* subsequence is a longest increasing subsequence under
  * the reversed order, so the same [[longest]] core runs once with `_ < _` and
  * once with `_ > _`. The imperative `Array`/`while` internals are confined to
  * [[longest]]; the public [[find]] signature is pure and total.
  */
object LongestSubsequences {

  def find(permutation: Permutation): MonotonicSubsequences = {
    val v = permutation.values
    MonotonicSubsequences(
      increasing = longest(v, (a, b) => a < b),
      decreasing = longest(v, (a, b) => a > b)
    )
  }

  /** A longest subsequence of `v` that is strictly monotone under `lessThan`. */
  private def longest(v: Vector[Int], lessThan: (Int, Int) => Boolean): Vector[Int] = {
    val n = v.length
    if (n == 0) Vector.empty
    else {
      val tails = ArrayBuffer.empty[Int] // tails(l) = index of best tail for length l+1
      val pred  = Array.fill(n)(-1)      // pred(i)  = previous index in the subsequence ending at i

      var i = 0
      while (i < n) {
        // Binary search for the first slot whose stored value is NOT lessThan v(i).
        var lo = 0
        var hi = tails.length
        while (lo < hi) {
          val mid = (lo + hi) >>> 1
          if (lessThan(v(tails(mid)), v(i))) lo = mid + 1 else hi = mid
        }
        if (lo > 0) pred(i) = tails(lo - 1)
        if (lo == tails.length) tails += i else tails(lo) = i
        i += 1
      }

      // Reconstruct from the index in the last (longest) slot.
      val out = ArrayBuffer.empty[Int]
      var idx = tails(tails.length - 1)
      while (idx != -1) {
        out += v(idx)
        idx = pred(idx)
      }
      out.reverseIterator.toVector
    }
  }
}
