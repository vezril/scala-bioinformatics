package bio.domain.protein

/** Result of the Rosalind PRTM ("Calculating Protein Mass") problem — the total
  * monoisotopic mass of a protein (the sum of its residues' monoisotopic masses).
  */
final case class ProteinMass(mass: Double) {

  /** Rosalind PRTM rendering: the total mass to three decimal places. */
  def format: String = f"$mass%.3f"
}
