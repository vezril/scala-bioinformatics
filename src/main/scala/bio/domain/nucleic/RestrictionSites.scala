package bio.domain.nucleic

/** Result of the Rosalind REVP ("Locating Restriction Sites") problem — every
  * reverse palindrome of length 4–12 in a DNA string, each carrying its 1-based
  * position and length.
  */
final case class RestrictionSites(sites: Vector[RestrictionSite]) {

  /** Rosalind REVP rendering: each site as `"$position $length"`, one per line.
    * The empty result renders as the empty string.
    */
  def format: String =
    sites.map(s => s"${s.position} ${s.length}").mkString("\n")
}
