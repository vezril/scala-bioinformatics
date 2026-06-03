package bio.domain.combinatorics

/** Result of the Rosalind REAR ("Reversal Distance") problem: the minimum number
  * of interval reversals required to transform one permutation into another (see
  * [[bio.algorithms.combinatorics.ReversalDistanceSearch.distance]]).
  *
  * `format` renders the distance as its decimal string.
  */
final case class ReversalDistance(distance: Int) {
  def format: String = distance.toString
}
