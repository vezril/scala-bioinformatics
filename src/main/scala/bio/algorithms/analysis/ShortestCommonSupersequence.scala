package bio.algorithms.analysis

import bio.domain.analysis.{Supersequence, SupersequenceProblem}

import scala.annotation.tailrec

/** Computes a shortest common supersequence of two DNA strings — Rosalind SCSP
  * ("Interleaving Two Motifs").
  *
  * A shortest common supersequence interleaves `s` and `t` around their longest common
  * subsequence: matched characters appear once, the rest in order. Its length is
  * `|s| + |t| − LCS(s,t)`.
  *
  * `dp(i)(j)` is the SCS length of the length-`i` prefix of `s` and the length-`j`
  * prefix of `t`. The table is filled with an imperative dynamic program (the
  * alignment-family `var`/`while`/`Array` exception), then backtracked from `(m,n)` by a
  * `@tailrec` walk that prepends characters to an immutable list. The public `build`
  * signature is pure and total.
  */
object ShortestCommonSupersequence {

  def build(problem: SupersequenceProblem): Supersequence = {
    val s = problem.s.value
    val t = problem.t.value
    val m = s.length
    val n = t.length

    val dp = Array.ofDim[Int](m + 1, n + 1)
    var i = 0
    while (i <= m) { dp(i)(0) = i; i += 1 }
    var j = 0
    while (j <= n) { dp(0)(j) = j; j += 1 }
    i = 1
    while (i <= m) {
      var jj = 1
      while (jj <= n) {
        dp(i)(jj) =
          if (s.charAt(i - 1) == t.charAt(jj - 1)) dp(i - 1)(jj - 1) + 1
          else 1 + math.min(dp(i - 1)(jj), dp(i)(jj - 1))
        jj += 1
      }
      i += 1
    }

    @tailrec
    def backtrack(i: Int, j: Int, acc: List[Char]): List[Char] =
      if (i == 0 && j == 0) acc
      else if (i == 0) backtrack(i, j - 1, t.charAt(j - 1) :: acc)
      else if (j == 0) backtrack(i - 1, j, s.charAt(i - 1) :: acc)
      else if (s.charAt(i - 1) == t.charAt(j - 1)) backtrack(i - 1, j - 1, s.charAt(i - 1) :: acc)
      else if (dp(i - 1)(j) <= dp(i)(j - 1)) backtrack(i - 1, j, s.charAt(i - 1) :: acc)
      else backtrack(i, j - 1, t.charAt(j - 1) :: acc)

    Supersequence(backtrack(m, n, Nil).mkString)
  }
}
