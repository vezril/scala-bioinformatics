package bio.algorithms.graph

import bio.domain.graph.UnrootedBinaryTreeLeafCount

/** Counts the internal nodes of any unrooted binary tree with `n` leaves
  * (Rosalind INOD — "Counting Phylogenetic Ancestors").
  *
  * **Closed-form identity.** In an unrooted binary tree every internal node has
  * degree exactly 3 and every leaf has degree exactly 1. A tree on `n + I` nodes
  * (where `I` is the number of internal nodes and `n` is the number of leaves)
  * has `n + I - 1` edges, and the handshake lemma gives
  *
  * `3 * I + 1 * n = 2 * (n + I - 1)`
  *
  * which simplifies to `I = n - 2`. The formula is exact for every `n >= 3`, so
  * no tree need actually be constructed.
  *
  * **Why the input is wrapped.** The Rosalind constraint is `3 <= n <= 10000`;
  * the framework's convention is to validate algorithm inputs through a smart
  * constructor and pass the wrapped value to the algorithm. See
  * [[UnrootedBinaryTreeLeafCount]] for the validation rules.
  *
  * **Complexity:** O(1).
  */
object PhylogeneticAncestors {

  def internalNodes(problem: UnrootedBinaryTreeLeafCount): Int =
    problem.n - 2
}
