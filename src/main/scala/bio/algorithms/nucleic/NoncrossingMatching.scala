package bio.algorithms.nucleic

import bio.domain.nucleic.NoncrossingMatchingProblem

/** Counts the noncrossing perfect matchings of basepair edges in an RNA
  * bonding graph (Rosalind spec 35 — CAT, "Catalan Numbers and RNA Secondary
  * Structures").
  *
  * A *noncrossing* matching is one in which no two edges `(i, j)` and `(k, l)`
  * satisfy `i < k < j < l` — biologically, this is the set of pseudoknot-free
  * secondary structures. The count follows a Catalan-flavoured recurrence
  * adapted to the basepair constraint: positions can only be bonded if their
  * symbols are complementary (`A↔U` or `C↔G`).
  *
  * **Recurrence.** Let `dp(i)(j)` be the number of noncrossing perfect
  * matchings on the inclusive substring `s[i..j]`. Then:
  *
  *   - `dp(i)(j) = 1` if `j < i` (empty interval — the empty matching);
  *   - `dp(i)(j) = 0` if `j - i + 1` is odd (no perfect matching on an odd
  *     number of nodes);
  *   - otherwise `dp(i)(j) = Σ_k dp(i+1)(k-1) · dp(k+1)(j)` summed over every
  *     `k ∈ {i+1, i+3, ..., j}` whose `s(k)` is the complementary basepair
  *     partner of `s(i)`. The step of 2 ensures `(k - i)` is odd, so both
  *     `[i+1, k-1]` and `[k+1, j]` have even length.
  *
  * The pairing `(i, k)` is the outermost bond on `s[i..j]`; what remains on
  * each side recurses independently.
  *
  * **Algorithm shape.** Bottom-up interval DP filling `dp` by increasing
  * **even** length (`len = 2, 4, ..., n`). Odd-length cells stay at their
  * initialised value of `0`. Total work `O(n³)` arithmetic ops, `O(n²)` memory.
  * At `n = 300` (the Rosalind cap) that's `27 · 10⁶` ops — milliseconds.
  *
  * **Modulo + overflow.** Rosalind asks for the answer modulo `1 000 000`.
  * The mod result fits in `Int`. The intermediate product `dp(a) * dp(b)`
  * (each operand `< 10⁶`) can reach `~10¹²`, exceeding `Int.MaxValue` —
  * hence the multiplication is performed in `Long`, then `mod`, then cast back.
  *
  * **Sister algorithm.** [[PerfectMatching]] (spec 34 — PMCH) counts *all*
  * perfect matchings (no noncrossing constraint) and admits the closed-form
  * `(#A)! · (#C)!`. The two specs share the input shape but diverge in the
  * counting question — and consequently in the algorithm: closed-form factorial
  * vs `O(n³)` interval DP.
  */
object NoncrossingMatching {

  private val Mod: Int = 1_000_000

  def count(problem: NoncrossingMatchingProblem): Int = {
    val s = problem.rna.value
    val n = s.length
    if (n == 0) 1
    else {
      val dp = Array.ofDim[Int](n, n)

      def get(i: Int, j: Int): Int = if (j < i) 1 else dp(i)(j)

      var len = 2
      while (len <= n) {
        var i = 0
        while (i <= n - len) {
          val j   = i + len - 1
          val si  = s.charAt(i)
          var sum = 0L
          var k   = i + 1
          while (k <= j) {
            if (isPair(si, s.charAt(k))) {
              sum += get(i + 1, k - 1).toLong * get(k + 1, j)
            }
            k += 2
          }
          dp(i)(j) = (sum % Mod).toInt
          i += 1
        }
        len += 2
      }

      dp(0)(n - 1)
    }
  }

  /** True iff `a` and `b` are complementary basepair partners (`A↔U` or
    * `C↔G`).
    */
  private def isPair(a: Char, b: Char): Boolean =
    (a == 'A' && b == 'U') || (a == 'U' && b == 'A') ||
      (a == 'C' && b == 'G') || (a == 'G' && b == 'C')
}
