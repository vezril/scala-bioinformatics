package bio.domain.combinatorics

/** Result of the Rosalind LGIS ("Longest Increasing Subsequence") problem: a
  * longest increasing subsequence and a longest decreasing subsequence of a
  * permutation (see [[bio.algorithms.combinatorics.LongestSubsequences.find]]).
  *
  * `format` renders the increasing values space-separated on the first line and
  * the decreasing values space-separated on the second line.
  */
final case class MonotonicSubsequences(increasing: Vector[Int], decreasing: Vector[Int]) {
  def format: String = Seq(increasing, decreasing).map(_.mkString(" ")).mkString("\n")
}
