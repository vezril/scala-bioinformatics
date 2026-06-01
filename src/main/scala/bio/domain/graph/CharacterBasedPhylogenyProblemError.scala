package bio.domain.graph

/** Construction failures for [[CharacterBasedPhylogenyProblem]] (Rosalind CHBP —
  * "Character-Based Phylogeny").
  */
sealed trait CharacterBasedPhylogenyProblemError
object CharacterBasedPhylogenyProblemError {

  /** No taxa were supplied. */
  case object EmptyTaxa extends CharacterBasedPhylogenyProblemError

  /** The same taxon name appears more than once. */
  final case class DuplicateTaxon(name: String) extends CharacterBasedPhylogenyProblemError

  /** The taxa count exceeded the Rosalind CHBP cap of 80. */
  final case class ExceedsMaximumTaxa(count: Int, max: Int)
      extends CharacterBasedPhylogenyProblemError

  /** A character row's length differs from the taxa count. */
  final case class RowLengthMismatch(rowIndex: Int, expected: Int, actual: Int)
      extends CharacterBasedPhylogenyProblemError

  /** A character row contains a symbol other than `'0'` or `'1'`. */
  final case class InvalidCharacter(rowIndex: Int, ch: Char)
      extends CharacterBasedPhylogenyProblemError

  /** Two characters induce conflicting splits — the table is inconsistent. */
  final case class ConflictingCharacters(rowIndexA: Int, rowIndexB: Int)
      extends CharacterBasedPhylogenyProblemError
}
