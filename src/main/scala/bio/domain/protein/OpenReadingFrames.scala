package bio.domain.protein

/** Result of the Rosalind ORF ("Open Reading Frames") problem — every distinct
  * candidate protein string translatable from an open reading frame across the
  * six reading frames of a DNA string.
  */
final case class OpenReadingFrames(proteins: Vector[ProteinString]) {

  /** Rosalind ORF rendering: each candidate protein on its own line. The empty
    * result renders as the empty string.
    */
  def format: String = proteins.map(_.value).mkString("\n")
}
