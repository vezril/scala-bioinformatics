package bio.domain.combinatorics

/** Result of the Rosalind SORT ("Sorting by Reversals") problem: the reversal
  * `distance` together with an ordered collection of `reversals` that, applied
  * successively to the source permutation, yields the target (see
  * [[bio.algorithms.combinatorics.ReversalSortingSearch.sort]]).
  *
  * `format` renders the distance on the first line followed by each reversal on
  * its own line; a zero-distance sorting formats to just the count.
  */
final case class ReversalSorting(distance: Int, reversals: Vector[Reversal]) {
  def format: String = (distance.toString +: reversals.map(_.format)).mkString("\n")
}
