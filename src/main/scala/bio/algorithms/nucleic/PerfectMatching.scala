package bio.algorithms.nucleic

import bio.domain.nucleic.PerfectMatchingProblem

/** Counts the perfect matchings of basepair edges in an RNA bonding graph
  * (Rosalind PMCH — "Perfect Matchings and RNA Secondary Structures").
  *
  * The bonding graph of an RNA string `s` has one node per nucleotide and
  * undirected edges only between A-U pairs and between C-G pairs (the
  * "basepair edges"). A *perfect matching of basepair edges* picks one A-U
  * edge per A (and per U) and one C-G edge per C (and per G).
  *
  * **Closed-form decoupling.** The A-U sub-problem and the C-G sub-problem
  * share no nodes — A's only neighbour symbol is U, and C's only neighbour is
  * G. So the count factors:
  *
  *   `perfectMatchings(s) = (#A)! · (#C)!`
  *
  * each factor counts how many ways to bijectively assign A's to U's (and
  * C's to G's). For the Rosalind sample `AGCUAGUCAU` with `#A = 3` and
  * `#C = 2`, the answer is `3! · 2! = 12`.
  *
  * **`BigInt` return.** The worst case at the Rosalind cap is `40! · 0! ≈
  * 8.16 · 10^47` — `Long` overflows at `~9.2 · 10^18`. Rosalind expects the
  * exact integer (no modulo), so the return type is `BigInt`.
  *
  * **Complexity:** `O(auCount + cgCount)` BigInt multiplications. At the
  * Rosalind cap that's at most 40 multiplications — microseconds.
  */
object PerfectMatching {

  def count(problem: PerfectMatchingProblem): BigInt =
    factorial(problem.auCount) * factorial(problem.cgCount)

  private def factorial(n: Int): BigInt =
    (BigInt(1) to BigInt(n)).foldLeft(BigInt(1))(_ * _)
}
