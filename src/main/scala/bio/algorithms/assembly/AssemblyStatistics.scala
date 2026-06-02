package bio.algorithms.assembly

import bio.domain.assembly.{AssemblyQuality, ContigCollection}

/** Computes assembly-contiguity N-statistics for the Rosalind ASMQ problem
  * ("Assessing Assembly Quality with N50 and N75").
  *
  * The N-statistic `NXX` is the maximum positive integer `L` such that the contigs
  * of length at least `L` account for at least `XX%` of the combined contig length.
  * It is found by sorting contig lengths in descending order, accumulating a
  * running total, and taking the length of the first contig at which the running
  * total reaches the threshold — because the included set grows monotonically as
  * `L` decreases, that length is exactly the maximum `L` satisfying the definition.
  *
  * The threshold test uses integer cross-multiplication
  * (`cumulative * 100 >= percentile * total`) so the boundary is exact, with no
  * floating-point rounding.
  */
object AssemblyStatistics {

  /** N50 and N75 for the collection. */
  def assess(contigs: ContigCollection): AssemblyQuality =
    AssemblyQuality(nStatistic(contigs, 50), nStatistic(contigs, 75))

  /** The `NXX` statistic for the given `percentile` (intended range 1..99).
    *
    * Total over a non-empty collection of positive-length contigs, since the
    * final cumulative equals the grand total and therefore always meets the
    * threshold.
    */
  def nStatistic(contigs: ContigCollection, percentile: Int): Int = {
    val lengths    = contigs.contigs.map(_.value.length)
    val total      = lengths.sum.toLong
    val sortedDesc = lengths.sorted(Ordering.Int.reverse)

    sortedDesc.iterator
      .scanLeft(0L)(_ + _)
      .drop(1) // align cumulative-after-include with each contig
      .zip(sortedDesc.iterator)
      .collectFirst {
        case (cumulative, length) if cumulative * 100 >= percentile.toLong * total =>
          length
      }
      .getOrElse(0) // unreachable for a valid (non-empty, positive-length) collection
  }
}
