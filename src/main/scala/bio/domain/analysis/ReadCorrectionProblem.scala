package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input for the Rosalind CORR ("Error Correction in Reads")
  * problem — see [[bio.algorithms.analysis.ReadErrorCorrection.correct]].
  *
  * Wraps a `Vector` of equal-length DNA reads. The smart constructor enforces,
  * with first-failure-wins ordering: at most 1000 reads, each read length ≤ 50
  * bp, and all reads of equal length (Hamming distance is only defined for
  * equal-length reads). Character validity (`A`,`C`,`G`,`T`) is enforced upstream
  * by [[DnaString]]. An empty read list is accepted (no corrections).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[ReadCorrectionProblem.from]].
  */
sealed abstract case class ReadCorrectionProblem(reads: Vector[DnaString])

object ReadCorrectionProblem {
  private val MaxReads: Int  = 1000
  private val MaxLength: Int = 50

  def from(
      reads: Vector[DnaString]
  ): Either[ReadCorrectionProblemError, ReadCorrectionProblem] =
    if (reads.size > MaxReads)
      Left(ReadCorrectionProblemError.TooManyReads(reads.size, MaxReads))
    else
      reads.find(_.value.length > MaxLength) match {
        case Some(r) =>
          Left(ReadCorrectionProblemError.ReadTooLong(r.value.length, MaxLength))
        case None =>
          val lengths = reads.map(_.value.length)
          if (lengths.distinct.size > 1)
            Left(ReadCorrectionProblemError.UnequalLengths(lengths))
          else
            Right(new ReadCorrectionProblem(reads) {})
      }
}
