package bio.algorithms.analysis

import bio.domain.analysis.{
  AlignmentBasedPhylogeny => Result,
  AlignmentBasedPhylogenyProblem,
  NamedSequence
}
import bio.domain.graph.NewickTree

/** Solves the Rosalind ALPH ("Alignment-Based Phylogeny") small-parsimony
  * problem via the classical *Sankoff* algorithm — see
  * [[bio.domain.analysis.AlignmentBasedPhylogenyProblem]].
  *
  * For a rooted binary tree `T` with named species at the leaves and a
  * multiple alignment of those species, the algorithm assigns DNA strings
  * (over `{A, C, G, T, -}`) to every internal node so that the sum of
  * per-edge Hamming distances is minimised.
  *
  * **Per-column DP.** Each column of the alignment is solved independently.
  * For each column:
  *
  *   1. **Bottom-up.** At each leaf, `cost(leaf, c) = 0` if `c` equals the
  *      leaf's column character, else `+∞`. At each internal node `u` with
  *      children `L` and `R`:
  *      `cost(u, c) = min_{c_L} (cost(L, c_L) + δ(c, c_L)) + min_{c_R} (cost(R, c_R) + δ(c, c_R))`
  *      where `δ(a, b) = 0` if `a == b` else `1` (gap is just a 5th symbol).
  *   2. **Top-down.** At the root, pick `c` minimising `cost(root, c)` (ties
  *      broken by lowest ordinal: `A < C < G < T < -`). At each internal
  *      node, for each child pick the symbol that realised the inner-min for
  *      the parent's chosen symbol. Recurse to leaves.
  *
  * The per-column root-min cost is added to `totalDistance`; the chosen
  * symbol per internal node is appended to that node's growing sequence.
  *
  * **Output ordering.** `internalAssignments` is returned in deterministic
  * pre-order traversal of the tree (root first, then recursively left
  * subtree then right subtree).
  *
  * **Complexity.** For `L` columns, `n` nodes, and alphabet size `K = 5`:
  * `O(L · n · K²)`. At the Rosalind cap (`L ≤ 300`, `n ≤ 999` total nodes
  * for 500 leaves, `K = 5`) this is `~7.5M` cell-ops — trivially fast.
  */
object AlignmentBasedPhylogeny {

  private val K: Int               = 5
  private val Symbols: Array[Char] = Array('A', 'C', 'G', 'T', '-')

  // Char -> symbol ordinal via a 128-slot lookup; -1 for invalid (won't be hit
  // because the smart constructor validates the alphabet up-front).
  private val SymbolIdx: Array[Int] = {
    val a = Array.fill(128)(-1)
    Symbols.zipWithIndex.foreach { case (c, i) => a(c.toInt) = i }
    a
  }

  /** Cost value used to represent "impossible" (+∞) — large enough to never
    * win a `min`, small enough that `Inf + 1` doesn't overflow.
    */
  private val Inf: Int = Int.MaxValue / 4

  /** Internal recursion result: the K-length cost-by-symbol array for this
    * node, plus the (already-computed) child `Costs` in tree-order.
    */
  private final case class Costs(cost: Array[Int], children: Vector[Costs])

  def solve(problem: AlignmentBasedPhylogenyProblem): Result = {
    val tree           = problem.tree
    val labelToSeq     = problem.alignment.iterator.map(r => r.label -> r.sequence).toMap
    val numColumns     = problem.alignment.head.sequence.length
    val internalLabels = collectInternalLabelsPreOrder(tree)

    // Accumulate per-internal-node sequence as we walk columns.
    val builders: Map[String, StringBuilder] =
      internalLabels.iterator.map(_ -> new StringBuilder).toMap

    var totalDistance = 0
    var col           = 0
    while (col < numColumns) {
      val (colCost, perInternalSymbol) = sankoffColumn(tree, labelToSeq, col)
      totalDistance += colCost
      perInternalSymbol.foreach { case (lbl, symIdx) =>
        builders(lbl).append(Symbols(symIdx))
      }
      col += 1
    }

    val internalAssignments =
      internalLabels.map(lbl => NamedSequence(lbl, builders(lbl).toString))

    Result(totalDistance, internalAssignments)
  }

  /** Run the Sankoff DP for a single column. Returns:
    *   - the column's contribution to `totalDistance` (the root's min cost);
    *   - a `Vector[(internalLabel, chosenSymbolOrdinal)]` in pre-order tree
    *     traversal, suitable for appending into per-internal builders.
    */
  private def sankoffColumn(
      tree: NewickTree,
      labelToSeq: Map[String, String],
      col: Int
  ): (Int, Vector[(String, Int)]) = {
    val rootCosts  = bottomUp(tree, labelToSeq, col)
    val rootSymbol = argMin(rootCosts.cost)
    val rootCost   = rootCosts.cost(rootSymbol)

    val out = Vector.newBuilder[(String, Int)]
    topDown(tree, rootCosts, rootSymbol, out)
    (rootCost, out.result())
  }

  /** Post-order recursion. At leaves builds the indicator cost vector; at
    * internal nodes folds the children's costs into the Sankoff recurrence.
    */
  private def bottomUp(
      node: NewickTree,
      labelToSeq: Map[String, String],
      col: Int
  ): Costs = {
    if (node.children.isEmpty) {
      val ch       = labelToSeq(node.label.get).charAt(col)
      val symbolIx = SymbolIdx(ch.toInt)
      val cost     = Array.fill(K)(Inf)
      cost(symbolIx) = 0
      Costs(cost, Vector.empty)
    } else {
      val childCosts = node.children.map(bottomUp(_, labelToSeq, col))
      val cost       = Array.fill(K)(0)
      var s          = 0
      while (s < K) {
        var contrib = 0
        var ci      = 0
        while (ci < childCosts.size) {
          val c = childCosts(ci)
          contrib += minTransitionCost(c.cost, s)
          ci += 1
        }
        cost(s) = contrib
        s += 1
      }
      Costs(cost, childCosts)
    }
  }

  /** Pre-order recursion. Each internal node emits its assigned symbol, then
    * for each child picks the symbol that realised the inner-min for the
    * parent's chosen symbol, and recurses with that.
    */
  private def topDown(
      node: NewickTree,
      costs: Costs,
      mySymbol: Int,
      out: scala.collection.mutable.Builder[(String, Int), Vector[(String, Int)]]
  ): Unit = {
    if (node.children.nonEmpty) {
      out += ((node.label.get, mySymbol))
      var i = 0
      while (i < node.children.size) {
        val childNode  = node.children(i)
        val childCosts = costs.children(i)
        val childSym   = argMinTransition(childCosts.cost, mySymbol)
        topDown(childNode, childCosts, childSym, out)
        i += 1
      }
    }
  }

  /** Minimum value of `childCost(c') + δ(parentSym, c')` over `c'`. */
  private def minTransitionCost(childCost: Array[Int], parentSym: Int): Int = {
    var best = Inf
    var cs   = 0
    while (cs < K) {
      val v = childCost(cs) + (if (cs == parentSym) 0 else 1)
      if (v < best) best = v
      cs += 1
    }
    best
  }

  /** Argmin of `childCost(c') + δ(parentSym, c')` over `c'`. Ties broken by
    * lowest ordinal (`A < C < G < T < -`).
    */
  private def argMinTransition(childCost: Array[Int], parentSym: Int): Int = {
    var bestSym = 0
    var bestVal = Inf
    var cs      = 0
    while (cs < K) {
      val v = childCost(cs) + (if (cs == parentSym) 0 else 1)
      if (v < bestVal) {
        bestVal = v
        bestSym = cs
      }
      cs += 1
    }
    bestSym
  }

  /** Argmin of an integer array. Ties broken by lowest index. */
  private def argMin(arr: Array[Int]): Int = {
    var best = 0
    var i    = 1
    while (i < arr.length) {
      if (arr(i) < arr(best)) best = i
      i += 1
    }
    best
  }

  /** Pre-order traversal of internal node labels (root first). */
  private def collectInternalLabelsPreOrder(t: NewickTree): Vector[String] =
    if (t.children.isEmpty) Vector.empty
    else t.children.foldLeft(Vector(t.label.get))(_ ++ collectInternalLabelsPreOrder(_))
}
