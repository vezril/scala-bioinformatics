package bio.algorithms.combinatorics

import bio.domain.combinatorics.{
  KmerComposition,
  KmerCompositionProblem,
  KmerEnumerationProblem
}

/** Computes the k-mer composition of a DNA string (Rosalind KMER): the number of
  * times each possible length-`k` DNA word occurs, reported in the lexicographic
  * order of those words.
  */
object ComposeKmers {

  /** The DNA alphabet in lexicographic order — `A < C < G < T`. */
  private val DnaAlphabet: Vector[Char] = Vector('A', 'C', 'G', 'T')

  /** One occurrence count per length-`problem.k` DNA word, in lexicographic order.
    *
    * The ordered k-mers are produced by reusing
    * [[EnumerateKmers.enumerate]] over the DNA alphabet. Because a
    * `KmerCompositionProblem` guarantees `1 <= k <= 10` and the DNA alphabet is
    * non-empty, duplicate-free, and within the symbol cap, the inner
    * [[KmerEnumerationProblem.from]] is *known* to succeed — so taking the `Right`
    * here is total.
    *
    * Counts come from sliding a length-`k` window over the sequence (overlapping
    * occurrences included) and tallying into a map; each ordered k-mer is then
    * looked up, defaulting to 0 when it never occurs (including when the sequence
    * is shorter than `k`, in which case there are no windows at all).
    */
  def compose(problem: KmerCompositionProblem): KmerComposition = {
    val k = problem.k
    val s = problem.dna.value

    val orderedKmers =
      EnumerateKmers
        .enumerate(KmerEnumerationProblem.from(DnaAlphabet, k).toOption.get)
        .kmers

    val tally: Map[String, Int] =
      s.sliding(k)
        .filter(_.length == k)
        .foldLeft(Map.empty[String, Int]) { (counts, window) =>
          counts.updated(window, counts.getOrElse(window, 0) + 1)
        }

    KmerComposition(orderedKmers.map(kmer => tally.getOrElse(kmer, 0)))
  }
}
