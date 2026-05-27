package bio.algorithms.combinatorics

import bio.domain.combinatorics.CombinationSumProblem

/** Computes `Σ_{k=m}^{n} C(n, k) mod 1_000_000` — the tail-sum of binomial
  * coefficients from row `n` of Pascal's triangle, starting at index `m` (Rosalind
  * ASPC).
  *
  * **Algorithm:** build row `n` of Pascal's triangle modularly via the recurrence
  * `C(n, k) = C(n-1, k-1) + C(n-1, k)` (all reduced mod `1_000_000`), then sum the
  * entries from index `m` to `n` accumulating modulo. The fold over `1 to n` keeps
  * only one row in memory at a time.
  *
  * **Why Pascal's triangle (not Fermat's little theorem):** `1_000_000 = 2^6 × 5^6`
  * is composite, so the closed-form `n! / (k! (n-k)!)` via modular inverse can't be
  * used safely (factorials share factors with the modulus). Pascal's triangle has no
  * such restriction and is simpler.
  *
  * **`Int` safety:** after each addition `acc ∈ [0, 999_999]`. The worst intermediate
  * is `999_999 + 999_999 = 1_999_998`, well within `Int.MaxValue ≈ 2.15 × 10^9`. No
  * overflow.
  *
  * **Complexity:** `O(n²)` integer additions. At `n ≤ 2000` that's ≤ 4M additions —
  * trivially fast. Memory: one `Vector[Int]` of length `n + 1` in flight.
  */
object Combinations {

  private val Modulus: Int = 1_000_000

  def sumFrom(problem: CombinationSumProblem): Int = {
    val row = buildModRow(problem.n, Modulus)
    (problem.m to problem.n).foldLeft(0) { (acc, k) =>
      (acc + row(k)) % Modulus
    }
  }

  /** Builds row `n` of Pascal's triangle, every value reduced modulo `modulus`. Row 0
    * is `Vector(1)`; each subsequent row is `[1] ++ pairwise-sums ++ [1]`.
    */
  private def buildModRow(n: Int, modulus: Int): Vector[Int] =
    (1 to n).foldLeft(Vector(1)) { (prev, _) =>
      val inner: Vector[Int] =
        (1 until prev.size).iterator.map(i => (prev(i - 1) + prev(i)) % modulus).toVector
      (1 +: inner) :+ 1
    }
}
