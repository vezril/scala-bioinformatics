package bio.domain.analysis

/** Construction failures for [[IsolatedSymbolsProblem]] (Rosalind OSYM —
  * "Isolating Symbols in Alignments").
  */
sealed trait IsolatedSymbolsProblemError
object IsolatedSymbolsProblemError {

  /** The left-hand DNA string exceeded the Rosalind OSYM cap of 1 000 bp. */
  final case class LeftTooLong(length: Int, max: Int)
      extends IsolatedSymbolsProblemError

  /** The right-hand DNA string exceeded the Rosalind OSYM cap of 1 000 bp. */
  final case class RightTooLong(length: Int, max: Int)
      extends IsolatedSymbolsProblemError
}
