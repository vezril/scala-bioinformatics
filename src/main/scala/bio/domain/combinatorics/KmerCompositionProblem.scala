package bio.domain.combinatorics

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind KMER ("k-Mer Composition") problem —
  * see [[bio.algorithms.combinatorics.ComposeKmers.compose]].
  *
  * Pairs a validated `dna` string with the word length `k`. The smart constructor
  * validates, first-failure-wins, in the order:
  *   1. `k >= 1`, else `NonPositiveK`;
  *   2. `k <= 10` (Rosalind cap), else `KExceedsMaximum`.
  *
  * The cap mirrors the LEXF enumeration bound, which guarantees that building the
  * ordered k-mers over the DNA alphabet always succeeds.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[KmerCompositionProblem.from]].
  */
sealed abstract case class KmerCompositionProblem(dna: DnaString, k: Int)

object KmerCompositionProblem {
  private val MaxK: Int = 10

  def from(
      dna: DnaString,
      k: Int
  ): Either[KmerCompositionProblemError, KmerCompositionProblem] =
    if (k < 1)
      Left(KmerCompositionProblemError.NonPositiveK(k))
    else if (k > MaxK)
      Left(KmerCompositionProblemError.KExceedsMaximum(k, MaxK))
    else
      Right(new KmerCompositionProblem(dna, k) {})
}
