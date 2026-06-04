package bio.domain.protein

/** Result of the Rosalind MPRT ("Finding a Protein Motif") problem for one
  * protein: its access `id` and the 1-based `positions` where the motif occurs
  * (see [[bio.algorithms.protein.MotifSearch.findLocations]]).
  *
  * `format` renders the id on the first line and the positions space-separated on
  * the second.
  */
final case class MotifLocations(id: String, positions: Vector[Int]) {
  def format: String = s"$id\n${positions.mkString(" ")}"
}
