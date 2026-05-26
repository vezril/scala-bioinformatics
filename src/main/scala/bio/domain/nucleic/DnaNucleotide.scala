package bio.domain.nucleic

sealed trait DnaNucleotide
object DnaNucleotide {
  case object A extends DnaNucleotide
  case object C extends DnaNucleotide
  case object G extends DnaNucleotide
  case object T extends DnaNucleotide

  val validChars: Set[Char] = Set('A', 'C', 'G', 'T')

  def fromChar(c: Char): Option[DnaNucleotide] = c match {
    case 'A' => Some(A)
    case 'C' => Some(C)
    case 'G' => Some(G)
    case 'T' => Some(T)
    case _   => None
  }

  def toChar(n: DnaNucleotide): Char = n match {
    case A => 'A'
    case C => 'C'
    case G => 'G'
    case T => 'T'
  }

  /** Watson-Crick base pair complement: A↔T, C↔G. Total and self-inverse. */
  def complement(n: DnaNucleotide): DnaNucleotide = n match {
    case A => T
    case T => A
    case C => G
    case G => C
  }
}
