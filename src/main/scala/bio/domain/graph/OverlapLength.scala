package bio.domain.graph

/** The overlap length `k` used to construct an overlap graph `O_k` — the number of
  * characters that must match between a suffix of one sequence and a prefix of another
  * to form a directed edge.
  *
  * Valid range: `k >= 1`. The upper bound is left implicit (effectively the length of
  * the shortest sequence in the input collection); `OverlapGraph.adjacency` handles
  * sequences shorter than `k` by emitting no edges involving them.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[OverlapLength.from]].
  */
sealed abstract case class OverlapLength(value: Int)

object OverlapLength {

  def from(value: Int): Either[OverlapLengthError, OverlapLength] =
    if (value < 1) Left(OverlapLengthError.NonPositive(value))
    else Right(new OverlapLength(value) {})
}
