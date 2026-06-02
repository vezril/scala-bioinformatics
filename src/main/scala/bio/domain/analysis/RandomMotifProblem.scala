package bio.domain.analysis

import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability

/** Parameters for the Rosalind RSTR ("Matching Random Motifs") problem —
  * see [[bio.algorithms.analysis.MatchingRandomMotifs.probability]].
  *
  * Holds a motif, a trial count `N`, and a GC-content fraction. The GC-content is a
  * `Probability` (a fraction in `[0,1]`) — NOT the percentage `GcContent` type.
  *
  * Constructable only via [[RandomMotifProblem.from]], which enforces the Rosalind
  * bounds with first-failure-wins ordering:
  *   - `motif.value.length <= 10`, else `MotifTooLong`
  *   - `trials >= 1`, else `NonPositiveTrials`
  *   - `trials <= 100000`, else `TooManyTrials`
  *
  * An empty motif is accepted (it yields a trivial probability of 1 in the algorithm).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class RandomMotifProblem(
    motif: DnaString,
    trials: Int,
    gcContent: Probability
)

object RandomMotifProblem {
  private val MaxMotifLength: Int = 10
  private val MaxTrials: Int      = 100000

  def from(
      motif: DnaString,
      trials: Int,
      gcContent: Probability
  ): Either[RandomMotifProblemError, RandomMotifProblem] = {
    val length = motif.value.length
    if (length > MaxMotifLength)
      Left(RandomMotifProblemError.MotifTooLong(length, MaxMotifLength))
    else if (trials < 1)
      Left(RandomMotifProblemError.NonPositiveTrials(trials))
    else if (trials > MaxTrials)
      Left(RandomMotifProblemError.TooManyTrials(trials, MaxTrials))
    else
      Right(new RandomMotifProblem(motif, trials, gcContent) {})
  }
}
