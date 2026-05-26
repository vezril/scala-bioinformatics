package bio.domain.protein

/** The result of looking a `Codon` up in the genetic code: either it codes for an
  * amino acid, or it is one of the three Stop signals.
  */
sealed trait CodonOutcome
object CodonOutcome {
  final case class AminoAcidProduct(aa: AminoAcid) extends CodonOutcome
  case object Stop                                  extends CodonOutcome
}
