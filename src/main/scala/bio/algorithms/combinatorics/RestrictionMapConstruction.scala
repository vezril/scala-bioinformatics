package bio.algorithms.combinatorics

import bio.domain.combinatorics.{RestrictionMap, RestrictionMapProblem}

/** Reconstructs a set of nonnegative positions from the multiset of its pairwise
  * distances — Rosalind PDPL ("Creating a Restriction Map"), the Turnpike / Partial
  * Digest problem.
  *
  * Uses Skiena's PLACE backtracking: the largest distance is the span between the
  * outermost points, so the map starts as `{0, width}`. Repeatedly, the largest
  * remaining distance `y` must come from a point at either `y` or `width - y`; a
  * candidate is accepted only if every distance from it to the already-placed points
  * is still available in the multiset, which is then consumed. When the multiset is
  * exhausted the placed points form a solution.
  *
  * Returns `None` when no set realises the multiset (e.g. `{1,1,1}`).
  *
  * Pure and total: the search is expressed with recursion and immutable `Map`/`Set`
  * values, with backtracking via `Option.orElse` — no `var`, `while`, or mutable
  * collection.
  */
object RestrictionMapConstruction {

  def solve(problem: RestrictionMapProblem): Option[RestrictionMap] = {
    val distances = problem.distances
    if (distances.isEmpty) Some(RestrictionMap(Vector(0)))
    else {
      val width  = distances.max
      val counts = toCounts(distances)
      place(decrement(counts, width), Set(0, width), width)
        .map(points => RestrictionMap(points.toVector.sorted))
    }
  }

  /** Place the remaining distances; `Some(x)` once the multiset is exhausted. */
  private def place(remaining: Map[Int, Int], x: Set[Int], width: Int): Option[Set[Int]] =
    if (remaining.isEmpty) Some(x)
    else {
      val y = remaining.keysIterator.max
      tryPoint(y, remaining, x, width)
        .orElse(tryPoint(width - y, remaining, x, width))
    }

  /** Try placing a point at `p`: accept only if every distance from `p` to the points
    * in `x` is still available, then consume those distances and recurse.
    */
  private def tryPoint(
      p: Int,
      remaining: Map[Int, Int],
      x: Set[Int],
      width: Int
  ): Option[Set[Int]] =
    if (x.contains(p)) None
    else {
      val dists = x.toVector.map(xi => math.abs(p - xi))
      removeAll(remaining, dists).flatMap(rest => place(rest, x + p, width))
    }

  /** Remove one occurrence of each distance, failing if any is unavailable. */
  private def removeAll(remaining: Map[Int, Int], dists: Vector[Int]): Option[Map[Int, Int]] =
    dists.foldLeft(Option(remaining)) { (acc, dist) =>
      acc.flatMap(m => if (m.getOrElse(dist, 0) > 0) Some(decrement(m, dist)) else None)
    }

  /** Decrement the count of `k`, dropping the key when it reaches zero. */
  private def decrement(m: Map[Int, Int], k: Int): Map[Int, Int] =
    m.get(k) match {
      case Some(c) if c > 1 => m.updated(k, c - 1)
      case Some(_)          => m - k
      case None             => m
    }

  /** Multiset as value -> count. */
  private def toCounts(distances: Vector[Int]): Map[Int, Int] =
    distances.groupBy(identity).map { case (value, occurrences) => value -> occurrences.size }
}
