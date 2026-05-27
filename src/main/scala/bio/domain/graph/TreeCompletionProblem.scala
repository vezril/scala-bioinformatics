package bio.domain.graph

/** Parameters for the tree-completion algorithm (Rosalind TREE).
  *
  * Constructable only via [[TreeCompletionProblem.from]] which enforces:
  *   - `1 <= n <= 1000` (node count within the Rosalind cap)
  *   - every edge's `u` and `v` are in `[1, n]` (endpoints reference real nodes)
  *
  * `UndirectedEdge.from` already enforces per-edge invariants (`u >= 1`, `v >= 1`,
  * `u != v`), so this bundle only adds the upper-bound check (`u <= n && v <= n`).
  * This division of responsibility — per-value invariants on the value type,
  * cross-input constraints on the bundle — matches the rest of the framework.
  *
  * Validation order: `n` lower bound, `n` upper bound, then per-edge endpoint scan
  * (first offending edge in input order wins).
  *
  * **Precondition (not enforced):** the input graph is expected to be acyclic. The
  * Rosalind problem guarantees this. The algorithm in `bio.algorithms.graph` trusts
  * the precondition — cycle detection is out of scope.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class TreeCompletionProblem(n: Int, edges: Vector[UndirectedEdge])

object TreeCompletionProblem {
  private val MaxN: Int = 1000

  def from(
      n: Int,
      edges: Vector[UndirectedEdge]
  ): Either[TreeCompletionProblemError, TreeCompletionProblem] =
    if (n < 1) Left(TreeCompletionProblemError.NonPositiveN(n))
    else if (n > MaxN) Left(TreeCompletionProblemError.NExceedsMaximum(n, MaxN))
    else
      edges.find(e => e.u > n || e.v > n) match {
        case Some(badEdge) =>
          Left(TreeCompletionProblemError.EdgeEndpointOutOfRange(badEdge, n))
        case None =>
          Right(new TreeCompletionProblem(n, edges) {})
      }
}
