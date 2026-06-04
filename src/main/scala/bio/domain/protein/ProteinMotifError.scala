package bio.domain.protein

/** Parse failures for [[ProteinMotif.parse]] (Rosalind MPRT). */
sealed trait ProteinMotifError
object ProteinMotifError {

  /** The motif shorthand was empty. */
  case object EmptyMotif extends ProteinMotifError

  /** A `[` or `{` group opened at `start` was never closed. */
  final case class UnterminatedGroup(start: Int) extends ProteinMotifError

  /** An unexpected character `char` was encountered at `index`. */
  final case class UnexpectedCharacter(char: Char, index: Int) extends ProteinMotifError
}
