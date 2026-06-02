package bio.domain.combinatorics

/** Result of the Rosalind PDPL ("Creating a Restriction Map") problem — a set `X` of
  * nonnegative positions whose difference multiset equals the input distance multiset
  * (see [[bio.algorithms.combinatorics.RestrictionMapConstruction.solve]]).
  *
  * Positions are held in ascending order; `format` renders them separated by single
  * spaces.
  */
final case class RestrictionMap(points: Vector[Int]) {

  def format: String = points.mkString(" ")
}
