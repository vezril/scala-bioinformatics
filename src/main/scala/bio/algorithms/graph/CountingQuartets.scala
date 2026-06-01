package bio.algorithms.graph

import bio.domain.graph.CountingQuartetsProblem

/** Counts the quartets consistent with an unrooted binary tree (Rosalind CNTQ —
  * "Counting Quartets").
  *
  * A quartet `AB|CD` is consistent with a tree `T` when it can be inferred from
  * one of `T`'s splits. Because `T` is a fully resolved unrooted binary tree,
  * **every** 4-element subset of its `n` taxa is resolved into exactly one of its
  * three quartet topologies — so the number of consistent quartets is simply the
  * number of 4-subsets:
  * `q(T) = C(n, 4) = n(n − 1)(n − 2)(n − 3) / 24`. The count is
  * topology-independent: it depends only on `n`.
  *
  * **Arithmetic.** The product of four consecutive integers is always divisible
  * by 24, so the division is exact and is performed before the modulus. For
  * `n ≤ 5000` the product is at most `5000⁴ = 6.25×10¹⁴`, comfortably within
  * `Long` range, so it is computed directly in `Long` and then reduced modulo
  * `1,000,000`. The result always lies in `[0, 999999]`.
  */
object CountingQuartets {

  private val Modulus: Long = 1000000L

  def count(problem: CountingQuartetsProblem): Int = {
    val n       = problem.n.toLong
    val product = n * (n - 1) * (n - 2) * (n - 3)
    ((product / 24) % Modulus).toInt
  }
}
