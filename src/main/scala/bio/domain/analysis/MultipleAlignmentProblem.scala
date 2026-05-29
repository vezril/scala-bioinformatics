package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind MULT ("Multiple Alignment")
  * problem — see [[bio.algorithms.analysis.MultipleAlignment.align]].
  *
  * Wraps exactly four DNA strings, each capped at the Rosalind length
  * limit (10 bp). The smart constructor enforces, first-failure-wins:
  *
  *   1. `strings.size == 4`, else `WrongNumberOfStrings(actual, 4)`;
  *   2. for the *first* index `i` in `0..3` where
  *      `strings(i).value.length > 10`, return
  *      `StringTooLong(i, length, 10)`.
  *
  * Empty strings (length 0) are accepted.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[MultipleAlignmentProblem.from]].
  */
sealed abstract case class MultipleAlignmentProblem(strings: Vector[DnaString])

object MultipleAlignmentProblem {
  private val ExpectedCount: Int = 4
  private val MaxLength: Int     = 10

  def from(
      strings: Vector[DnaString]
  ): Either[MultipleAlignmentProblemError, MultipleAlignmentProblem] = {
    if (strings.size != ExpectedCount)
      Left(MultipleAlignmentProblemError.WrongNumberOfStrings(strings.size, ExpectedCount))
    else {
      val offending = strings.iterator.zipWithIndex
        .collectFirst { case (s, i) if s.value.length > MaxLength => (i, s.value.length) }
      offending match {
        case Some((i, len)) =>
          Left(MultipleAlignmentProblemError.StringTooLong(i, len, MaxLength))
        case None =>
          Right(new MultipleAlignmentProblem(strings) {})
      }
    }
  }
}
