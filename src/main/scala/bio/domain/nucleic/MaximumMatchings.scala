package bio.domain.nucleic

/** Result of the Rosalind MMCH ("Maximum Matchings and RNA Secondary
  * Structures") problem: the exact total number of maximum matchings of
  * basepair edges in the RNA bonding graph, held as a [[scala.math.BigInt]]
  * since the count overflows `Long` at the length cap.
  */
final case class MaximumMatchings(count: BigInt) {
  def format: String = count.toString
}
