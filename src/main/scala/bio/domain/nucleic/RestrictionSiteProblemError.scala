package bio.domain.nucleic

/** Construction failures for [[RestrictionSiteProblem]] (Rosalind REVP — "Locating
  * Restriction Sites").
  */
sealed trait RestrictionSiteProblemError
object RestrictionSiteProblemError {

  /** The DNA sequence exceeded the Rosalind REVP cap of 1000 bp (1 kbp). */
  final case class SequenceTooLong(length: Int, max: Int)
      extends RestrictionSiteProblemError
}
