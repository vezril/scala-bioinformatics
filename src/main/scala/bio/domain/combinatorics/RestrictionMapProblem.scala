package bio.domain.combinatorics

/** Validated input for the Rosalind PDPL ("Creating a Restriction Map") problem —
  * the Turnpike / Partial Digest problem. See
  * [[bio.algorithms.combinatorics.RestrictionMapConstruction.solve]].
  *
  * Wraps the distance multiset `L` as a `Vector[Int]`. The smart constructor enforces,
  * first failure wins:
  *   1. `|L|` is triangular — there is some `n >= 1` with `n(n-1)/2 = |L|` (the number
  *      of pairwise distances among `n` points), else `InvalidSize`;
  *   2. every distance is positive (index order), else `NonPositiveDistance`.
  *
  * The empty multiset (`|L| = 0`, `n = 1`) is accepted and yields the trivial map `{0}`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[RestrictionMapProblem.from]].
  */
sealed abstract case class RestrictionMapProblem(distances: Vector[Int])

object RestrictionMapProblem {

  def from(
      distances: Vector[Int]
  ): Either[RestrictionMapProblemError, RestrictionMapProblem] =
    if (!isTriangular(distances.size))
      Left(RestrictionMapProblemError.InvalidSize(distances.size))
    else
      firstNonPositive(distances) match {
        case Some(err) => Left(err)
        case None      => Right(new RestrictionMapProblem(distances) {})
      }

  /** True when `size = n(n-1)/2` for some `n >= 1` (size is a triangular number). */
  private def isTriangular(size: Int): Boolean = {
    val n = ((1.0 + math.sqrt(1.0 + 8.0 * size)) / 2.0).round.toInt
    n >= 1 && n * (n - 1) / 2 == size
  }

  /** The first non-positive distance (index order), if any. */
  private def firstNonPositive(distances: Vector[Int]): Option[RestrictionMapProblemError] =
    distances.iterator.zipWithIndex.collectFirst {
      case (value, index) if value <= 0 =>
        RestrictionMapProblemError.NonPositiveDistance(index, value)
    }
}
