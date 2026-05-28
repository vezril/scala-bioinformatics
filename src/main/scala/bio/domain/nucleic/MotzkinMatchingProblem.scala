package bio.domain.nucleic

/** Validated input bundle for the Rosalind MOTZ ("Motzkin Numbers and RNA
  * Secondary Structures") problem — see
  * [[bio.algorithms.nucleic.MotzkinMatching.count]].
  *
  * Wraps an [[RnaString]] with **only** the length cap (`length <= 300`).
  * Unlike [[PerfectMatchingProblem]] (spec 34 — PMCH) and
  * [[NoncrossingMatchingProblem]] (spec 35 — CAT), this wrapper does **not**
  * enforce balanced AU or CG counts: partial noncrossing matchings always
  * exist (at minimum, the empty matching), so any RNA string — including
  * unbalanced, odd-length, and entirely-`A` inputs — is a valid problem.
  *
  * The empty RNA string is accepted (the empty matching alone counts as `1`).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[MotzkinMatchingProblem.from]].
  */
sealed abstract case class MotzkinMatchingProblem(rna: RnaString)

object MotzkinMatchingProblem {
  private val MaxLength: Int = 300

  def from(
      rna: RnaString
  ): Either[MotzkinMatchingProblemError, MotzkinMatchingProblem] = {
    val len = rna.value.length
    if (len > MaxLength)
      Left(MotzkinMatchingProblemError.ExceedsMaxLength(len, MaxLength))
    else
      Right(new MotzkinMatchingProblem(rna) {})
  }
}
