package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind LCSM ("Finding a Shared Motif")
  * problem — see [[bio.algorithms.analysis.SharedMotif.find]].
  *
  * Wraps a collection of DNA strings whose *longest common substring* the
  * algorithm will find. The smart constructor enforces:
  *   1. `sequences.nonEmpty`, else `EmptyCollection`;
  *   2. `sequences.size <= 100` (Rosalind cap), else `TooManyStrings`;
  *   3. each string `length <= 1000` (per-string cap), else `StringTooLong`.
  *
  * Empty strings within the collection are accepted — the algorithm
  * short-circuits to `""` (the empty string is a substring of every string,
  * including itself).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[SharedMotifProblem.from]].
  */
sealed abstract case class SharedMotifProblem(sequences: Vector[DnaString])

object SharedMotifProblem {
  private val MaxStrings: Int = 100
  private val MaxLength: Int  = 1000

  def from(
      sequences: Vector[DnaString]
  ): Either[SharedMotifProblemError, SharedMotifProblem] = {
    if (sequences.isEmpty)
      Left(SharedMotifProblemError.EmptyCollection)
    else if (sequences.size > MaxStrings)
      Left(SharedMotifProblemError.TooManyStrings(sequences.size, MaxStrings))
    else {
      val firstOversized = sequences.iterator.zipWithIndex.collectFirst {
        case (s, i) if s.value.length > MaxLength =>
          SharedMotifProblemError.StringTooLong(i, s.value.length, MaxLength)
      }
      firstOversized match {
        case Some(err) => Left(err)
        case None      => Right(new SharedMotifProblem(sequences) {})
      }
    }
  }
}
