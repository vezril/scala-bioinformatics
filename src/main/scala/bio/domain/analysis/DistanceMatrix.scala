package bio.domain.analysis

/** Result of the Rosalind PDST ("Creating a Distance Matrix") problem: the
  * `n × n` p-distance matrix `D` with `D[i][j] = d_p(s_i, s_j)` (see
  * [[bio.algorithms.analysis.PDistanceMatrix.compute]]).
  *
  * `format` renders each value to 5 decimal places, entries space-separated,
  * rows joined by newlines; an empty matrix formats to the empty string.
  */
final case class DistanceMatrix(rows: Vector[Vector[Double]]) {
  def format: String = rows.map(_.map(v => f"$v%.5f").mkString(" ")).mkString("\n")
}
