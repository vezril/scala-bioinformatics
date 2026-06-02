package bio.domain.analysis

/** Result of the Rosalind EVAL ("Expected Number of Restriction Sites") problem —
  * the expected number of occurrences of the motif in a random DNA string, one per
  * input GC-content, in the same order as the GC-contents.
  */
final case class ExpectedRestrictionSites(expectations: Vector[Double]) {

  /** Rosalind EVAL rendering: each expected count to three decimal places, separated
    * by single spaces. The empty result renders as the empty string.
    */
  def format: String = expectations.map(e => f"$e%.3f").mkString(" ")
}
