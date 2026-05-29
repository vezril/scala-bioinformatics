package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind KSIM ("Finding All Similar
  * Motifs") problem — see
  * [[bio.algorithms.analysis.SimilarMotifs.findAll]].
  *
  * Wraps an edit-distance budget `k` together with two DNA strings whose
  * roles are **asymmetric**: `motif` (`s`) is the pattern searched for, and
  * `genome` (`t`) is the text searched within for substrings `t′` with
  * edit distance `d_E(s, t′) <= k`.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `1 <= k <= 50`, else `KOutOfRange`;
  *   2. `motif.value.length <= 5000`, else `MotifTooLong`;
  *   3. `genome.value.length <= 50000`, else `GenomeTooLong`.
  *
  * Empty `motif` and/or empty `genome` are accepted.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[SimilarMotifsProblem.from]].
  */
sealed abstract case class SimilarMotifsProblem(
    k: Int,
    motif: DnaString,
    genome: DnaString
)

object SimilarMotifsProblem {
  private val MinK: Int            = 1
  private val MaxK: Int            = 50
  private val MaxMotifLength: Int  = 5000
  private val MaxGenomeLength: Int = 50000

  def from(
      k: Int,
      motif: DnaString,
      genome: DnaString
  ): Either[SimilarMotifsProblemError, SimilarMotifsProblem] = {
    val motifLen  = motif.value.length
    val genomeLen = genome.value.length
    if (k < MinK || k > MaxK)
      Left(SimilarMotifsProblemError.KOutOfRange(k, MinK, MaxK))
    else if (motifLen > MaxMotifLength)
      Left(SimilarMotifsProblemError.MotifTooLong(motifLen, MaxMotifLength))
    else if (genomeLen > MaxGenomeLength)
      Left(SimilarMotifsProblemError.GenomeTooLong(genomeLen, MaxGenomeLength))
    else
      Right(new SimilarMotifsProblem(k, motif, genome) {})
  }
}
