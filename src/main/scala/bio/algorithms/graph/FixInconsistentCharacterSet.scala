package bio.algorithms.graph

import bio.domain.graph.{ConsistentCharacterTable, InconsistentCharacterSetProblem}

/** Repairs an inconsistent character table by deleting a single conflicting row
  * (Rosalind CSET — "Fixing an Inconsistent Character Set").
  *
  * Each row is a character whose `'1'`/`'0'` columns form a split of the taxa.
  * Two characters *conflict* iff all four cross-intersections of their sides are
  * non-empty; a table is *consistent* iff no pair conflicts.
  *
  * **Algorithm:** A single row `r` removes every conflict iff `r` lies in every
  * conflicting pair — and therefore in the *first* one. So:
  *   1. Enumerate the conflicting pairs `(i, j)` once.
  *   2. If there are none, the table is already consistent — drop the first row
  *      to satisfy the "delete a single row" contract.
  *   3. Otherwise the only candidates are the two endpoints of the first
  *      conflicting pair. Test them smallest-index-first; delete the first that
  *      occurs in *every* conflicting pair. If neither does, no single deletion
  *      suffices and the result is `None`.
  *
  * Retained rows keep their original order. A pure function;
  * `None` only when a single-row repair is impossible (Rosalind guarantees one
  * exists for its inputs). **Complexity:** `O(m² · n)` to find conflicts (`m`
  * rows, `n` taxa), then `O(#conflicts)` to pick the row.
  */
object FixInconsistentCharacterSet {

  def fix(problem: InconsistentCharacterSetProblem): Option[ConsistentCharacterTable] = {
    val rows = problem.rows

    val conflicts: Vector[(Int, Int)] =
      (for {
        i <- rows.indices.iterator
        j <- (i + 1 until rows.size).iterator
        if conflict(rows(i), rows(j))
      } yield (i, j)).toVector

    val rowToDelete: Option[Int] =
      conflicts.headOption match {
        case None         => rows.indices.headOption // already consistent
        case Some((a, b)) =>
          Vector(a, b).find(c => conflicts.forall { case (x, y) => x == c || y == c })
      }

    rowToDelete.map(c => ConsistentCharacterTable(rows.patch(c, Nil, 1)))
  }

  /** Whether two character rows' splits conflict — all four cross-intersections
    * of their `'1'`/`'0'` index sets are non-empty.
    */
  private def conflict(a: String, b: String): Boolean = {
    val a1 = ones(a)
    val a0 = zeros(a)
    val b1 = ones(b)
    val b0 = zeros(b)
    a1.intersect(b1).nonEmpty && a1.intersect(b0).nonEmpty &&
    a0.intersect(b1).nonEmpty && a0.intersect(b0).nonEmpty
  }

  private def ones(row: String): Set[Int]  = row.indices.filter(row(_) == '1').toSet
  private def zeros(row: String): Set[Int] = row.indices.filter(row(_) == '0').toSet
}
