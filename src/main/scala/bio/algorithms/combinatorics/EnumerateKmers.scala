package bio.algorithms.combinatorics

import bio.domain.combinatorics.{KmerEnumeration, KmerEnumerationProblem}

/** Enumerates every length-`n` string over an ordered alphabet (Rosalind LEXF).
  *
  * The k-mers are produced in lexicographic order, where the *given* alphabet
  * order defines the ordering of symbols.
  */
object EnumerateKmers {

  /** All `length`-symbol strings over `problem.alphabet`, in lexicographic order.
    *
    * Builds the ordered Cartesian product 𝒜ⁿ with a left fold that grows each
    * prefix by one symbol per step. Because the prefixes (built so far) form the
    * outer iteration and the alphabet the inner one, the most-significant
    * position varies slowest — so the output is already lexicographic and needs
    * no explicit sort.
    */
  def enumerate(problem: KmerEnumerationProblem): KmerEnumeration = {
    val kmers =
      (1 to problem.length).foldLeft(Vector("")) { (prefixes, _) =>
        for {
          prefix <- prefixes
          symbol <- problem.alphabet
        } yield prefix + symbol
      }
    KmerEnumeration(kmers)
  }
}
