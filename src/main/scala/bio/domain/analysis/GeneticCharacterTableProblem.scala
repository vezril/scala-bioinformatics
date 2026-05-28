package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind "Creating a Character Table from
  * Genetic Strings" problem (spec 33) — see
  * [[bio.algorithms.analysis.GeneticCharacterTable.compute]].
  *
  * Bundles a collection of equal-length, alphabet-validated DNA strings
  * together with their precomputed row count (`size`) and common column count
  * (`length`). The smart constructor enforces the Rosalind input contract:
  *   1. non-empty input;
  *   2. `1 <= size <= 100`;
  *   3. every string length in `1..300`;
  *   4. all strings the same length;
  *   5. every column has at most two distinct symbols ("characterizable").
  *
  * Validation order is fixed and first-failure-wins so that error messages
  * always reference the most-specific defect — see the spec for the exact
  * cascade.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[GeneticCharacterTableProblem.from]].
  */
sealed abstract case class GeneticCharacterTableProblem(
    sequences: Vector[DnaString],
    size: Int,
    length: Int
)

object GeneticCharacterTableProblem {
  private val MaxStrings: Int = 100
  private val MaxLength: Int  = 300

  def from(
      sequences: Vector[DnaString]
  ): Either[GeneticCharacterTableProblemError, GeneticCharacterTableProblem] = {
    if (sequences.isEmpty)
      Left(GeneticCharacterTableProblemError.EmptyInput)
    else if (sequences.size > MaxStrings)
      Left(GeneticCharacterTableProblemError.TooManyStrings(sequences.size, MaxStrings))
    else {
      val firstOversized = sequences.iterator.zipWithIndex.collectFirst {
        case (s, i) if s.value.length > MaxLength =>
          GeneticCharacterTableProblemError.StringTooLong(i, s.value.length, MaxLength)
      }
      firstOversized match {
        case Some(err) => Left(err)
        case None =>
          val expectedLength = sequences.head.value.length
          val firstMismatch  = sequences.iterator.zipWithIndex.collectFirst {
            case (s, i) if s.value.length != expectedLength =>
              GeneticCharacterTableProblemError.InconsistentLength(
                i,
                expectedLength,
                s.value.length
              )
          }
          firstMismatch match {
            case Some(err) => Left(err)
            case None =>
              firstNonCharacterizableColumn(sequences, expectedLength) match {
                case Some(err) => Left(err)
                case None =>
                  Right(
                    new GeneticCharacterTableProblem(
                      sequences,
                      sequences.size,
                      expectedLength
                    ) {}
                  )
              }
          }
      }
    }
  }

  /** Scans each column left-to-right; returns a `NonCharacterizable` error for
    * the first column whose distinct-symbol count exceeds 2.
    */
  private def firstNonCharacterizableColumn(
      sequences: Vector[DnaString],
      length: Int
  ): Option[GeneticCharacterTableProblemError] = {
    val columns = 0 until length
    columns.iterator
      .map { j =>
        val distinct = sequences.iterator.map(_.value.charAt(j)).toSet
        (j, distinct.size)
      }
      .collectFirst {
        case (j, count) if count > 2 =>
          GeneticCharacterTableProblemError.NonCharacterizable(j, count)
      }
  }
}
