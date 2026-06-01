package bio.domain.graph

/** Construction failures for [[QuartetsProblem]] (Rosalind QRT — "Quartets"). */
sealed trait QuartetsProblemError
object QuartetsProblemError {

  /** No taxon names were supplied — at least one taxon is required. */
  case object EmptyTaxa extends QuartetsProblemError

  /** A taxon name occurred more than once. Carries the first repeated name. */
  final case class DuplicateTaxon(name: String) extends QuartetsProblemError

  /** No character rows were supplied — at least one character is required. */
  case object EmptyTable extends QuartetsProblemError

  /** A character row's length differed from the taxon count. `rowIndex`
    * identifies the first offending row, `expected` is the taxon count, and
    * `actual` is the row's length.
    */
  final case class InconsistentWidth(rowIndex: Int, expected: Int, actual: Int)
      extends QuartetsProblemError

  /** A character row contained a symbol other than `0`, `1`, or `x`.
    * `rowIndex` and `colIndex` identify the first offending position and
    * `symbol` is the offending character.
    */
  final case class InvalidSymbol(rowIndex: Int, colIndex: Int, symbol: Char)
      extends QuartetsProblemError
}
