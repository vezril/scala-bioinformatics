package bio.algorithms.nucleic

import bio.domain.nucleic.MotzkinMatchingProblem

/** Counts the noncrossing matchings — *partial* matchings allowed — of
  * basepair edges in an RNA bonding graph (Rosalind spec 36 — MOTZ, "Motzkin
  * Numbers and RNA Secondary Structures").
  *
  * Unlike [[NoncrossingMatching]] (CAT, spec 35), MOTZ does **not** require
  * every node to be bonded. The empty matching, every single-bond matching,
  * every two-bond matching that is noncrossing, etc. — all count. Biologically,
  * this enumerates every pseudoknot-free secondary structure (not just the
  * fully-paired ones).
  *
  * **Recurrence.** Let `M(i)(j)` be the number of noncrossing matchings on the
  * inclusive substring `s[i..j]`. Then:
  *
  *   - `M(i)(j) = 1` if `j < i` (empty interval — the empty matching);
  *   - otherwise
  *     `M(i)(j) = M(i+1)(j) + Σ_k M(i+1)(k-1) · M(k+1)(j)`,
  *     summed over every `k ∈ {i+1, ..., j}` whose `s(k)` is the complementary
  *     basepair partner of `s(i)`.
  *
  * The first term covers the case where position `i` is *unbonded*; the sum
  * covers every choice of bond partner for `i`. The single-character base case
  * `M(i)(i) = 1` falls out of the recurrence: `M(i+1)(i) = 1` (empty interval)
  * and the sum is empty (no `k > i = j`), so `M(i)(i) = 1 + 0 = 1`.
  *
  * **Departure from CAT.** Two changes relative to spec 35:
  *   1. The extra `M(i+1)(j)` term — position `i` may be unbonded.
  *   2. No `(k - i)` parity constraint — partial sub-matchings exist on
  *      intervals of any length, so `k` ranges over every position in `i+1..j`,
  *      not just the odd-offset ones.
  *
  * **Note on the name.** Rosalind frames the problem via Motzkin numbers, but
  * the values this algorithm produces are *not* literal Motzkin numbers `M_n`
  * (which assume the complete graph `K_n` where every pair of nodes can be
  * bonded). Our RNA bonding graph restricts bonds to `A`-`U` and `C`-`G`, so
  * the counts are the RNA-constrained variant — typically smaller than the
  * corresponding `M_n`.
  *
  * **Algorithm shape.** Bottom-up interval DP filling `M` by increasing length
  * `len = 1, 2, ..., n`. Total work `O(n³)` arithmetic ops, `O(n²)` memory.
  * At `n = 300` (the Rosalind cap) that's `27 · 10⁶` ops — milliseconds.
  *
  * **Modulo + overflow.** Same treatment as CAT: result mod `1 000 000`,
  * intermediate products in `Long` to avoid `Int` overflow before the mod.
  *
  * **Sister algorithms.** [[PerfectMatching]] (PMCH, spec 34) counts *all*
  * perfect matchings — `(#A)! · (#C)!`, no noncrossing constraint.
  * [[NoncrossingMatching]] (CAT, spec 35) counts noncrossing *perfect*
  * matchings via the Catalan-flavoured recurrence. The three specs form the
  * "all matchings / noncrossing perfect / noncrossing partial" trio on the
  * same RNA-bonding-graph input shape.
  */
object MotzkinMatching {

  private val Mod: Int = 1_000_000

  def count(problem: MotzkinMatchingProblem): Int = {
    val s = problem.rna.value
    val n = s.length
    if (n == 0) 1
    else {
      val m = Array.ofDim[Int](n, n)

      def get(i: Int, j: Int): Int = if (j < i) 1 else m(i)(j)

      var len = 1
      while (len <= n) {
        var i = 0
        while (i <= n - len) {
          val j   = i + len - 1
          val si  = s.charAt(i)
          var sum = get(i + 1, j).toLong // position i unbonded
          var k   = i + 1
          while (k <= j) {
            if (isPair(si, s.charAt(k))) {
              sum += get(i + 1, k - 1).toLong * get(k + 1, j)
            }
            k += 1
          }
          m(i)(j) = (sum % Mod).toInt
          i += 1
        }
        len += 1
      }

      m(0)(n - 1)
    }
  }

  /** True iff `a` and `b` are complementary basepair partners (`A↔U` or
    * `C↔G`).
    */
  private def isPair(a: Char, b: Char): Boolean =
    (a == 'A' && b == 'U') || (a == 'U' && b == 'A') ||
      (a == 'C' && b == 'G') || (a == 'G' && b == 'C')
}
