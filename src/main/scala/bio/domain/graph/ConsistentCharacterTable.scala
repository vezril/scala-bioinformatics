package bio.domain.graph

/** A consistent `0`/`1` character table — the result of
  * [[bio.algorithms.graph.FixInconsistentCharacterSet.fix]]. Holds the retained
  * character rows (in their original input order); no two of them conflict.
  *
  * @param rows the character rows, each a `0/1` string over the taxa columns
  */
final case class ConsistentCharacterTable(rows: Vector[String]) {

  /** The rows rendered one per line. */
  def format: String = rows.mkString("\n")
}
