package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input for the Rosalind SCSP ("Interleaving Two Motifs") problem — see
  * [[bio.algorithms.analysis.ShortestCommonSupersequence.build]].
  *
  * Wraps the two DNA strings `s` and `t`, each of length `≤ 1000` bp (keeping the
  * `O(m·n)` reconstruction table tractable). The smart constructor enforces the
  * per-sequence cap with first-failure-wins ordering (`s` then `t`); empty sequences
  * are accepted.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[SupersequenceProblem.from]].
  */
sealed abstract case class SupersequenceProblem(s: DnaString, t: DnaString)

object SupersequenceProblem {
  private val MaxLength: Int = 1000

  def from(s: DnaString, t: DnaString): Either[SupersequenceProblemError, SupersequenceProblem] =
    if (s.value.length > MaxLength)
      Left(SupersequenceProblemError.SequenceTooLong(s.value.length, MaxLength))
    else if (t.value.length > MaxLength)
      Left(SupersequenceProblemError.SequenceTooLong(t.value.length, MaxLength))
    else
      Right(new SupersequenceProblem(s, t) {})
}
