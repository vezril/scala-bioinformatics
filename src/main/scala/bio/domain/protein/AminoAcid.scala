package bio.domain.protein

/** The 20 standard amino acids, modeled as a sealed ADT.
  *
  * Each case object exposes its canonical single-letter IUPAC code via [[code]].
  */
sealed trait AminoAcid {
  def code: Char
}

object AminoAcid {
  case object F extends AminoAcid { val code: Char = 'F' }
  case object L extends AminoAcid { val code: Char = 'L' }
  case object I extends AminoAcid { val code: Char = 'I' }
  case object V extends AminoAcid { val code: Char = 'V' }
  case object S extends AminoAcid { val code: Char = 'S' }
  case object P extends AminoAcid { val code: Char = 'P' }
  case object T extends AminoAcid { val code: Char = 'T' }
  case object A extends AminoAcid { val code: Char = 'A' }
  case object Y extends AminoAcid { val code: Char = 'Y' }
  case object H extends AminoAcid { val code: Char = 'H' }
  case object N extends AminoAcid { val code: Char = 'N' }
  case object D extends AminoAcid { val code: Char = 'D' }
  case object Q extends AminoAcid { val code: Char = 'Q' }
  case object K extends AminoAcid { val code: Char = 'K' }
  case object E extends AminoAcid { val code: Char = 'E' }
  case object C extends AminoAcid { val code: Char = 'C' }
  case object R extends AminoAcid { val code: Char = 'R' }
  case object G extends AminoAcid { val code: Char = 'G' }
  case object W extends AminoAcid { val code: Char = 'W' }
  case object M extends AminoAcid { val code: Char = 'M' }

  /** All 20 amino acids, in the canonical order used in this codebase. */
  val all: Vector[AminoAcid] = Vector(F, L, I, V, S, P, T, A, Y, H, N, D, Q, K, E, C, R, G, W, M)

  /** The set of valid single-letter codes (used by [[ProteinString.from]]). */
  val validCodes: Set[Char] = all.map(_.code).toSet
}
