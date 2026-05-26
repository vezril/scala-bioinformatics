package bio.domain.nucleic

sealed trait RnaNucleotide
object RnaNucleotide {
  case object A extends RnaNucleotide
  case object C extends RnaNucleotide
  case object G extends RnaNucleotide
  case object U extends RnaNucleotide

  val validChars: Set[Char] = Set('A', 'C', 'G', 'U')

  def fromChar(c: Char): Option[RnaNucleotide] = c match {
    case 'A' => Some(A)
    case 'C' => Some(C)
    case 'G' => Some(G)
    case 'U' => Some(U)
    case _   => None
  }

  def toChar(n: RnaNucleotide): Char = n match {
    case A => 'A'
    case C => 'C'
    case G => 'G'
    case U => 'U'
  }
}
