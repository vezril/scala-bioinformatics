package bio.domain.nucleic

final case class RnaNucleotideCounts(a: Int, c: Int, g: Int, u: Int) {
  def format: String = s"$a $c $g $u"
}
