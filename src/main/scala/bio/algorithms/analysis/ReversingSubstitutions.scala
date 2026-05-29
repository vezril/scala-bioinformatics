package bio.algorithms.analysis

import bio.domain.analysis.{
  ReversingSubstitution,
  ReversingSubstitutionsProblem
}
import bio.domain.graph.NewickTree

/** Enumerates every *reversing substitution* in a fully-labelled rooted
  * binary tree of DNA sequences (Rosalind spec 45 — RSUB, "Identifying
  * Reversing Substitutions").
  *
  * A reversing substitution at position `i` is two parent-child edges
  * `(s, t)` and `(v, w)` on a root-down path satisfying:
  *
  *   1. `s` is an ancestor of `w`; the edge `(s, t)` precedes `(v, w)`.
  *   2. `s[i] == w[i] != v[i] == t[i]` — original at `s`, substituted at
  *      `t`, the same substituted value at `v`, then reverted to the
  *      original at `w`.
  *   3. For every node `u` on the path from `t` to `v` (inclusive),
  *      `t[i] == u[i]` — the substituted value is preserved end-to-end with
  *      no intervening substitution at position `i`.
  *
  * **Algorithm — per-column, per-edge DFS.** For each position `i ∈ [0, L)`
  * and each directed parent-child edge `(s, t)` of the tree:
  *
  *   - if `s[i] == t[i]`: skip (no first substitution).
  *   - else: let `X = s[i]` (original), `Y = t[i]` (substituted). DFS from
  *     `t` into descendants whose `[i] == Y`. For each child `c` of the
  *     current node:
  *       * if `c[i] == Y`: continue DFS into `c`.
  *       * if `c[i] == X`: emit `ReversingSubstitution(t.label, c.label, i+1,
  *         X, Y, X)`. Do NOT recurse into `c` — its position-`i` value is
  *         no longer `Y` so it cannot anchor any *further* path of this
  *         reversion.
  *       * else (third symbol): stop this branch — condition 3 is violated
  *         for any deeper descendant.
  *
  * **Complexity.** `O(L · n · n)` worst case (per position, per edge, full
  * subtree DFS). With `L ≤ 400` and `n ≤ 100`, that's `~4M` ops — trivial.
  *
  * **Output order.** Position-ascending, then a pre-order traversal of the
  * first-substitution edge. Rosalind accepts any order; this ordering is
  * purely a quality-of-life choice for tests and diffs.
  */
object ReversingSubstitutions {

  def findAll(problem: ReversingSubstitutionsProblem): Vector[ReversingSubstitution] = {
    val tree       = problem.tree
    val labelToSeq = problem.alignment.iterator.map(r => r.label -> r.sequence).toMap
    val numColumns = problem.alignment.head.sequence.length

    val out = Vector.newBuilder[ReversingSubstitution]

    var i = 0
    while (i < numColumns) {
      walkEdges(tree, labelToSeq, i, out)
      i += 1
    }

    out.result()
  }

  /** Pre-order walk of every parent-child edge `(parent, child)`. For each
    * such edge where `parent[i] != child[i]`, kick off the reversion DFS
    * inside `child`'s subtree.
    */
  private def walkEdges(
      node: NewickTree,
      labelToSeq: Map[String, String],
      i: Int,
      out: scala.collection.mutable.Builder[ReversingSubstitution, Vector[ReversingSubstitution]]
  ): Unit = {
    if (node.children.nonEmpty) {
      val parentSeq = labelToSeq(node.label.get)
      val parentCh  = parentSeq.charAt(i)
      node.children.foreach { child =>
        val childCh = labelToSeq(child.label.get).charAt(i)
        if (parentCh != childCh) {
          // Candidate first substitution at this edge.
          findReversionsBelow(child, labelToSeq, i, parentCh, childCh, out)
        }
        // Continue pre-order walk into child's edges (independent of whether
        // this edge was a candidate).
        walkEdges(child, labelToSeq, i, out)
      }
    }
  }

  /** DFS inside `t`'s subtree, preserving the substituted value `y` along
    * the path, emitting a reversion whenever a child carries the original
    * `x` (and stopping the branch on any third symbol).
    *
    * @param t       the child node of the first-substitution edge `(s, t)`;
    *                its `[i]` value equals `y`.
    * @param x       original symbol — `s[i]`.
    * @param y       substituted symbol — `t[i]`.
    */
  private def findReversionsBelow(
      t: NewickTree,
      labelToSeq: Map[String, String],
      i: Int,
      x: Char,
      y: Char,
      out: scala.collection.mutable.Builder[ReversingSubstitution, Vector[ReversingSubstitution]]
  ): Unit = {
    val tLabel = t.label.get
    descend(t)

    def descend(u: NewickTree): Unit = {
      // u's [i] is known to equal y at entry.
      u.children.foreach { c =>
        val cCh = labelToSeq(c.label.get).charAt(i)
        if (cCh == y) {
          descend(c)
        } else if (cCh == x) {
          out += ReversingSubstitution(
            firstChangeSpecies = tLabel,
            reversionSpecies = c.label.get,
            position = i + 1,
            originalSymbol = x,
            substitutedSymbol = y,
            revertedSymbol = x
          )
          // Do not recurse into c — the reversion ends here.
        }
        // else: third symbol; condition 3 is violated, stop this branch.
      }
    }
  }
}
