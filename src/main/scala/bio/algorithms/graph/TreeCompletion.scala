package bio.algorithms.graph

import bio.domain.graph.TreeCompletionProblem

/** Computes the minimum number of edges that must be added to an acyclic undirected
  * graph on `n` nodes to make it a tree. (Rosalind TREE.)
  *
  * **Math:** A forest on `n` nodes with `m` edges has exactly `c = n − m` connected
  * components. Connecting `c` components into a single tree requires exactly `c − 1`
  * additional edges. Therefore the answer is:
  *
  * {{{
  *   edgesToAdd = c − 1 = (n − m) − 1 = n − m − 1
  * }}}
  *
  * The graph topology beyond `(n, m)` is irrelevant — the answer depends only on the
  * node count and the edge count.
  *
  * **Precondition (not verified):** the input graph must be acyclic (a forest). The
  * Rosalind problem guarantees this. We do not run cycle detection because (a) the
  * math is identical regardless of how the components are arranged, and (b) verifying
  * the precondition would require a union-find pre-pass we don't otherwise need.
  *
  * **Complexity:** O(1). Reads `n` and `edges.size` from the validated bundle and
  * does a single subtraction.
  *
  * **Result range:** under the Rosalind precondition (input is a forest), `m ≤ n − 1`,
  * so the result is always ≥ 0. If a caller violated the precondition by passing a
  * graph with cycles (so `m ≥ n`), this function would return a negative number —
  * caveat lector.
  */
object TreeCompletion {

  def edgesToAdd(problem: TreeCompletionProblem): Int =
    problem.n - problem.edges.size - 1
}
