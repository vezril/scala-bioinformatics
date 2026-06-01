package bio.algorithms.graph

import bio.domain.graph.{NewickTree, Split, SplitDistanceProblem}

import scala.collection.immutable.BitSet

/** Computes the split distance between two unrooted binary trees (Rosalind SPTD
  * — "Phylogeny Comparison with Split Distance").
  *
  * Removing one edge of an unrooted tree partitions its taxa into two sides — a
  * *split*. A leaf edge isolates a single taxon (a trivial split); an internal
  * edge yields a *nontrivial* split with at least two taxa per side. An unrooted
  * binary tree on `n` taxa has exactly `n − 3` internal edges, hence `n − 3`
  * nontrivial splits. The split distance is
  * `d_split(T₁, T₂) = 2(n − 3) − 2·s`, where `s` is the number of nontrivial
  * splits the two trees share.
  *
  * **Algorithm.** Index the taxa `0 .. n−1`. For each tree, a single recursive
  * traversal computes, for every node, the leaf-index set beneath it; an internal
  * node whose leaf set is nontrivial (`2 ≤ |A| ≤ n − 2`) contributes the
  * canonical [[Split]] `Split.of(A, universe \ A)`. Collecting each tree's splits
  * into a `Set[Split]` deduplicates them; the shared count `s` is the size of the
  * intersection. Both trees use the same taxon indexing, so equal canonical
  * splits compare equal across trees.
  *
  * **Complexity.** `O(n²)` bitset work per tree (up to `n` nodes, each unioning
  * `O(n)`-bit sets) — comfortable for `n ≤ 3,000`.
  */
object SplitDistance {

  def compute(problem: SplitDistanceProblem): Int = {
    val taxa     = problem.taxa
    val n        = taxa.size
    val index    = taxa.zipWithIndex.toMap
    val universe = BitSet(taxa.indices: _*)

    val shared =
      nontrivialSplits(problem.tree1, index, universe, n)
        .intersect(nontrivialSplits(problem.tree2, index, universe, n))
        .size

    2 * (n - 3) - 2 * shared
  }

  /** The set of nontrivial splits of `tree` over the indexed taxa. */
  private def nontrivialSplits(
      tree: NewickTree,
      index: Map[String, Int],
      universe: BitSet,
      n: Int
  ): Set[Split] = {
    def go(node: NewickTree): (BitSet, Set[Split]) =
      if (node.children.isEmpty)
        (node.label.flatMap(index.get).fold(BitSet.empty)(BitSet(_)), Set.empty)
      else {
        val (leafSet, childSplits) =
          node.children.foldLeft((BitSet.empty, Set.empty[Split])) {
            case ((leaves, splits), child) =>
              val (childLeaves, childSplitsAcc) = go(child)
              (leaves | childLeaves, splits ++ childSplitsAcc)
          }
        val size = leafSet.size
        val here =
          if (size >= 2 && size <= n - 2) childSplits + Split.of(leafSet, universe.diff(leafSet))
          else childSplits
        (leafSet, here)
      }

    go(tree)._2
  }
}
