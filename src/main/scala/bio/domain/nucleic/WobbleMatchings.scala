package bio.domain.nucleic

/** Result of the Rosalind RNAS ("Wobble Bonding and RNA Secondary Structures")
  * problem: the exact total number of valid noncrossing matchings in the RNA
  * bonding graph, held as a [[scala.math.BigInt]] since the count overflows
  * `Long` even on the sample input.
  */
final case class WobbleMatchings(count: BigInt) {
  def format: String = count.toString
}
