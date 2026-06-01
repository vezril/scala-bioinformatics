package bio.domain.graph

/** Construction failures for [[InconsistentCharacterSetProblem]] (Rosalind CSET —
  * "Fixing an Inconsistent Character Set").
  */
sealed trait InconsistentCharacterSetProblemError
object InconsistentCharacterSetProblemError {

  /** No character rows were supplied. */
  case object EmptyTable extends InconsistentCharacterSetProblemError

  /** A row's width differs from the first row's width. */
  final case class RaggedTable(rowIndex: Int, expected: Int, actual: Int)
      extends InconsistentCharacterSetProblemError

  /** The taxa count (columns) exceeded the Rosalind CSET cap of 100. */
  final case class ExceedsMaximumTaxa(count: Int, max: Int)
      extends InconsistentCharacterSetProblemError

  /** A row contains a symbol other than `'0'` or `'1'`. */
  final case class InvalidCharacter(rowIndex: Int, ch: Char)
      extends InconsistentCharacterSetProblemError
}
