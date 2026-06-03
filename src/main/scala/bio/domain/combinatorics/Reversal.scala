package bio.domain.combinatorics

/** A single reversal in a sorting-by-reversals solution (Rosalind SORT),
  * encoded by the 1-based endpoints of the interval it inverts (`from < to`).
  *
  * `format` renders the endpoints space-separated, the Rosalind line shape.
  */
final case class Reversal(from: Int, to: Int) {
  def format: String = s"$from $to"
}
