package bio.algorithms.nucleic

import bio.domain.nucleic.{WobbleMatchingProblem, WobbleMatchings}

/** Counts every valid noncrossing matching — *partial* matchings allowed — in
  * the RNA bonding graph for Rosalind RNAS ("Wobble Bonding and RNA Secondary
  * Structures").
  *
  * This is the wobble-and-minimum-loop variant of [[MotzkinMatching]] (MOTZ,
  * spec 36). Three departures from MOTZ:
  *
  *   1. **Wobble pairing.** In addition to the Watson–Crick pairs `A`–`U` and
  *      `C`–`G`, an edge may also join `U`–`G` (the wobble pair).
  *   2. **Minimum separation.** An edge may connect positions `i < k` only when
  *      `k >= i + 4` (no hairpin shorter than three unpaired bases between the
  *      partners).
  *   3. **Exact arithmetic.** The counts overflow `Long` even on the sample, so
  *      the DP runs in exact [[scala.math.BigInt]] with **no** modular
  *      reduction (unlike MOTZ/CAT).
  *
  * **Recurrence.** Let `M(i)(j)` be the number of valid noncrossing matchings
  * on the inclusive substring `s[i..j]`, with `M(i)(j) = 1` for `j < i`. Then
  *
  *   `M(i)(j) = M(i+1)(j) + Σ_{k=i+4}^{j} [pair(s_i, s_k)] · M(i+1)(k-1) · M(k+1)(j)`,
  *
  * where the first term leaves position `i` unbonded and the sum bonds `i` to
  * each admissible partner `k`. The answer is `M(0)(n-1)` (and `1` for the
  * empty string).
  *
  * **Algorithm shape.** Bottom-up interval DP filling `M` by increasing length.
  * `O(n³)` `BigInt` operations, `O(n²)` memory; at `n = 200` (the Rosalind cap)
  * that is well under a second. The imperative `var`/`while` table fill is
  * confined here, mirroring [[MotzkinMatching]]; the public [[count]] signature
  * is pure and total.
  */
object WobbleMatching {

  def count(problem: WobbleMatchingProblem): WobbleMatchings = {
    val s = problem.rna.value
    val n = s.length
    if (n == 0) WobbleMatchings(BigInt(1))
    else {
      val m = Array.fill(n, n)(BigInt(0))

      def get(i: Int, j: Int): BigInt = if (j < i) BigInt(1) else m(i)(j)

      var len = 1
      while (len <= n) {
        var i = 0
        while (i <= n - len) {
          val j   = i + len - 1
          val si  = s.charAt(i)
          var sum = get(i + 1, j) // position i unbonded
          var k   = i + 4
          while (k <= j) {
            if (isPair(si, s.charAt(k))) {
              sum += get(i + 1, k - 1) * get(k + 1, j)
            }
            k += 1
          }
          m(i)(j) = sum
          i += 1
        }
        len += 1
      }

      WobbleMatchings(m(0)(n - 1))
    }
  }

  /** True iff `a` and `b` may form a bond: a Watson–Crick pair (`A`–`U` or
    * `C`–`G`) or a wobble pair (`U`–`G`).
    */
  private def isPair(a: Char, b: Char): Boolean =
    (a == 'A' && b == 'U') || (a == 'U' && b == 'A') ||
      (a == 'C' && b == 'G') || (a == 'G' && b == 'C') ||
      (a == 'U' && b == 'G') || (a == 'G' && b == 'U')
}
