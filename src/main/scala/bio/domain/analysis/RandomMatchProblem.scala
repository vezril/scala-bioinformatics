package bio.domain.analysis

import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability

/** Parameters for the random-string-matching algorithm (Rosalind PROB).
  *
  * Constructable only via [[RandomMatchProblem.from]] which enforces the Rosalind
  * upper bounds:
  *   - `dna.value.length <= 100`
  *   - `gcContents.size <= 20`
  *
  * Empty DNA and empty GC-content arrays are accepted (they produce well-defined
  * trivial outputs in the algorithm — see [[bio.algorithms.analysis.RandomMatch]]).
  *
  * Validation order: DNA length first, then GC-content array size. First failure wins.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class RandomMatchProblem(
    dna: DnaString,
    gcContents: Vector[Probability]
)

object RandomMatchProblem {
  private val MaxDnaLength: Int   = 100
  private val MaxGcContents: Int  = 20

  def from(
      dna: DnaString,
      gcContents: Vector[Probability]
  ): Either[RandomMatchProblemError, RandomMatchProblem] =
    if (dna.value.length > MaxDnaLength)
      Left(RandomMatchProblemError.DnaTooLong(dna.value.length, MaxDnaLength))
    else if (gcContents.size > MaxGcContents)
      Left(RandomMatchProblemError.TooManyGcContents(gcContents.size, MaxGcContents))
    else
      Right(new RandomMatchProblem(dna, gcContents) {})
}
