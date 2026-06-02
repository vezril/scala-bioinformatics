package bio.domain.protein

/** Construction failures for [[ProteinMassProblem]] (Rosalind PRTM — "Calculating
  * Protein Mass").
  */
sealed trait ProteinMassProblemError
object ProteinMassProblemError {

  /** The protein exceeded the Rosalind PRTM cap of 1000 aa. */
  final case class ProteinTooLong(length: Int, max: Int)
      extends ProteinMassProblemError
}
