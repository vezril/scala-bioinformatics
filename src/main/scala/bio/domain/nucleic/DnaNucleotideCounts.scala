package bio.domain.nucleic

final case class DnaNucleotideCounts(a: Int, c: Int, g: Int, t: Int) {
  def format: String = s"$a $c $g $t"
}
