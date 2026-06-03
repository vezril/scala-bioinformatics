package bio.domain.nucleic

/** Validated input bundle for the Rosalind RNAS ("Wobble Bonding and RNA
  * Secondary Structures") problem — see
  * [[bio.algorithms.nucleic.WobbleMatching.count]].
  *
  * Wraps an [[RnaString]] with **only** the length cap (`length <= 200`).
  * Like [[MotzkinMatchingProblem]] (spec 36 — MOTZ), this wrapper does **not**
  * enforce balanced base counts: partial noncrossing matchings always exist (at
  * minimum, the empty matching), so any RNA string — including unbalanced,
  * odd-length, and entirely-`A` inputs — is a valid problem. The empty RNA
  * string is accepted (the empty matching alone counts as `1`).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[WobbleMatchingProblem.from]].
  */
sealed abstract case class WobbleMatchingProblem(rna: RnaString)

object WobbleMatchingProblem {
  private val MaxLength: Int = 200

  def from(
      rna: RnaString
  ): Either[WobbleMatchingProblemError, WobbleMatchingProblem] = {
    val len = rna.value.length
    if (len > MaxLength)
      Left(WobbleMatchingProblemError.SequenceTooLong(len, MaxLength))
    else
      Right(new WobbleMatchingProblem(rna) {})
  }
}
