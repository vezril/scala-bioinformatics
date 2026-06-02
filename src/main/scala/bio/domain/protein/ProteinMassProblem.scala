package bio.domain.protein

/** Validated input for the Rosalind PRTM ("Calculating Protein Mass") problem —
  * see [[bio.algorithms.protein.ProteinMass.calculate]].
  *
  * Wraps a [[ProteinString]] of length `≤ 1000` aa. Characters are already validated
  * by `ProteinString`, so the only additional rule is the length cap:
  *   - `protein.value.length <= 1000`, else `ProteinTooLong`.
  *
  * Empty proteins are accepted (total mass `0`).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[ProteinMassProblem.from]].
  */
sealed abstract case class ProteinMassProblem(protein: ProteinString)

object ProteinMassProblem {
  private val MaxLength: Int = 1000

  def from(
      protein: ProteinString
  ): Either[ProteinMassProblemError, ProteinMassProblem] = {
    val length = protein.value.length
    if (length > MaxLength)
      Left(ProteinMassProblemError.ProteinTooLong(length, MaxLength))
    else
      Right(new ProteinMassProblem(protein) {})
  }
}
