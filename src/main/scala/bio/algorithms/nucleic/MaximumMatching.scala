package bio.algorithms.nucleic

import bio.domain.nucleic.{MaximumMatchingProblem, MaximumMatchings}

/** Counts the maximum matchings of basepair edges in an RNA bonding graph
  * (Rosalind MMCH — "Maximum Matchings and RNA Secondary Structures").
  *
  * The A-U and C-G subgraphs are vertex-disjoint, so the count of maximum
  * matchings factors. On the complete bipartite A-U subgraph with `a` A's and
  * `u` U's, a maximum matching has `min(a, u)` edges, and the number of such
  * matchings is the falling factorial
  *
  *   `P(hi, lo) = hi · (hi-1) · … · (hi-lo+1)`,  `hi = max(a, u)`, `lo = min(a, u)`
  *
  * (choose, in order, which `lo` of the `hi` larger-side nodes get matched). The
  * C-G subgraph contributes the analogous factor, so
  *
  *   `maximumMatchings(s) = P(max(a,u), min(a,u)) · P(max(c,g), min(c,g))`.
  *
  * For the sample `AUGCUUC` (`a=1, u=3, c=2, g=1`): `P(3,1) · P(2,1) = 3 · 2 = 6`.
  *
  * This is the unbalanced generalisation of [[PerfectMatching]] (PMCH), which is
  * the special case `a = u`, `c = g` where each factor collapses to a factorial.
  * The counts overflow `Long`, so the return is exact `BigInt`. Work is `O(n)`
  * multiplications.
  */
object MaximumMatching {

  def count(problem: MaximumMatchingProblem): MaximumMatchings = {
    val au = fallingFactorial(problem.aCount, problem.uCount)
    val cg = fallingFactorial(problem.cCount, problem.gCount)
    MaximumMatchings(au * cg)
  }

  /** Falling factorial `max(x,y) · (max-1) · … · (max-min+1)` — the number of
    * maximum matchings on a complete bipartite graph with `x` and `y` nodes per
    * side. Equals `1` when either side is empty.
    */
  private def fallingFactorial(x: Int, y: Int): BigInt = {
    val hi = math.max(x, y)
    val lo = math.min(x, y)
    (BigInt(hi - lo + 1) to BigInt(hi)).foldLeft(BigInt(1))(_ * _)
  }
}
