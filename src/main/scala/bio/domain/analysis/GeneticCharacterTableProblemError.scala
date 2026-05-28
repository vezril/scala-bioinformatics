package bio.domain.analysis

/** Construction failures for [[GeneticCharacterTableProblem]]. */
sealed trait GeneticCharacterTableProblemError
object GeneticCharacterTableProblemError {

  /** The input collection was empty — at least one DNA string is required. */
  case object EmptyInput extends GeneticCharacterTableProblemError

  /** The input collection exceeded the Rosalind cap of 100 strings. */
  final case class TooManyStrings(count: Int, max: Int)
      extends GeneticCharacterTableProblemError

  /** A DNA string exceeded the Rosalind per-string length cap of 300 bp.
    * `index` identifies the first offending row.
    */
  final case class StringTooLong(index: Int, length: Int, max: Int)
      extends GeneticCharacterTableProblemError

  /** Row lengths must be identical for column-wise iteration. `index` identifies
    * the first row whose length differs from `expected` (the length of row 0).
    */
  final case class InconsistentLength(index: Int, expected: Int, actual: Int)
      extends GeneticCharacterTableProblemError

  /** A column was not characterizable — it contained more than 2 distinct
    * symbols. `columnIndex` identifies the first offending column.
    */
  final case class NonCharacterizable(columnIndex: Int, distinctCount: Int)
      extends GeneticCharacterTableProblemError
}
