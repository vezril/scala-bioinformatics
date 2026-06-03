package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input for the Rosalind PDST ("Creating a Distance Matrix")
  * problem — see [[bio.algorithms.analysis.PDistanceMatrix.compute]].
  *
  * Wraps a `Vector` of equal-length `DnaString`s. The smart constructor
  * enforces, with first-failure-wins ordering: at most 10 strings, each string
  * length ≤ 1000 bp, and all strings of equal length (the p-distance is only
  * defined for equal-length strings). Character validity (`A`,`C`,`G`,`T`) is
  * enforced upstream by [[DnaString]]. An empty string list is accepted (a
  * degenerate `0 × 0` matrix).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[DistanceMatrixProblem.from]].
  */
sealed abstract case class DistanceMatrixProblem(strings: Vector[DnaString])

object DistanceMatrixProblem {
  private val MaxStrings: Int = 10
  private val MaxLength: Int  = 1000

  def from(
      strings: Vector[DnaString]
  ): Either[DistanceMatrixProblemError, DistanceMatrixProblem] =
    if (strings.size > MaxStrings)
      Left(DistanceMatrixProblemError.TooManyStrings(strings.size, MaxStrings))
    else
      strings.find(_.value.length > MaxLength) match {
        case Some(s) =>
          Left(DistanceMatrixProblemError.StringTooLong(s.value.length, MaxLength))
        case None =>
          val lengths = strings.map(_.value.length)
          if (lengths.distinct.size > 1)
            Left(DistanceMatrixProblemError.UnequalLengths(lengths))
          else
            Right(new DistanceMatrixProblem(strings) {})
      }
}
