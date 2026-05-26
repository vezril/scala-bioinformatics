package bio.domain.protein

/** A string of amino-acid single-letter codes, validated to contain only the 20 valid
  * codes (as defined by `AminoAcid`).
  *
  * Implemented as `sealed abstract case class` to prevent Scala 2.13's case-class
  * `apply` and `copy` synthesis from leaking around the smart constructor. Construct
  * via [[ProteinString.from]] (validates input), [[ProteinString.fromAminoAcids]]
  * (total — every input is valid by construction), or [[ProteinString.unsafeFrom]]
  * (internal use only).
  */
sealed abstract case class ProteinString(value: String)

object ProteinString {

  def from(s: String): Either[ProteinError, ProteinString] =
    s.find(!AminoAcid.validCodes.contains(_)) match {
      case Some(ch) => Left(ProteinError.InvalidCharacter(ch))
      case None     => Right(new ProteinString(s) {})
    }

  /** Total assembly of a protein from already-typed amino acids. By construction
    * every code is valid, so no validation is needed.
    */
  def fromAminoAcids(aas: Seq[AminoAcid]): ProteinString =
    new ProteinString(aas.iterator.map(_.code).mkString) {}

  /** Internal constructor for trusted callers that guarantee `s` is a valid protein
    * string (i.e., every char is in `AminoAcid.validCodes`). Bypasses validation.
    */
  private[bio] def unsafeFrom(s: String): ProteinString = new ProteinString(s) {}
}
