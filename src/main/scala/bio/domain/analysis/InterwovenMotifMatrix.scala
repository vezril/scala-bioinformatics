package bio.domain.analysis

/** Result of the Rosalind ITWV ("Finding Disjoint Motifs in a Gene") problem:
  * the `n × n` 0/1 matrix `M` with `M[j][k] = 1` iff patterns `j` and `k` can be
  * interwoven into the text (see
  * [[bio.algorithms.analysis.InterwovenMotifs.compute]]).
  *
  * `format` renders each row's entries joined by a single space, with rows joined
  * by newlines; an empty matrix formats to the empty string.
  */
final case class InterwovenMotifMatrix(rows: Vector[Vector[Int]]) {
  def format: String = rows.map(_.mkString(" ")).mkString("\n")
}
