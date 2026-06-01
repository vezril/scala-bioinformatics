package bio.algorithms.graph

import bio.domain.graph.{CharacterBasedPhylogenyProblem, NewickTree}

/** Reconstructs an unrooted binary tree from a consistent character table
  * (Rosalind CHBP — "Character-Based Phylogeny"). The inverse of
  * [[bio.algorithms.graph.CharacterTable.compute]].
  *
  * Each character row is a split `S | Sᶜ` of the taxa (the `'1'`-side vs the
  * `'0'`-side). For a *consistent* table, orienting every split so it excludes a
  * fixed reference taxon makes the retained sides a **laminar family** (any two
  * are nested or disjoint) — exactly the internal clusters of the tree rooted at
  * the reference edge. The tree can then be read directly off the nesting.
  *
  * **Algorithm:**
  *   1. Index the taxa `0 … n−1`; use index `0` as the reference.
  *   2. For each character, take the set of `'1'`-indices; if it contains the
  *      reference, replace it with its complement so the reference is excluded.
  *   3. Keep only *internal* clusters — both sides have `≥ 2` taxa
  *      (`2 ≤ |S| ≤ n−2`). Singleton/all-equal splits are trivial leaf edges,
  *      already represented by the leaves. Deduplicate.
  *   4. Recurse from the full taxa set: a node's children are the *maximal*
  *      clusters strictly inside it (disjoint, by laminarity) plus every member
  *      not covered by one (a leaf). A single-member set is a leaf.
  *
  * Children are ordered by their smallest taxon index for deterministic output;
  * Rosalind grades the unrooted topology, so order is irrelevant to correctness.
  *
  * A pure, total function — every input accepted by
  * [[CharacterBasedPhylogenyProblem.from]] (which rejects inconsistent tables)
  * yields a well-defined tree. **Complexity:** `O(k²)` in the cluster count
  * (`k ≤ n + #characters`); microseconds at the `n ≤ 80` cap.
  */
object CharacterBasedPhylogeny {

  def build(problem: CharacterBasedPhylogenyProblem): NewickTree = {
    val taxa = problem.taxa
    val n    = taxa.size

    val clusters: Vector[Set[Int]] =
      problem.characters
        .map(orientAwayFromReference)
        .filter(side => side.size >= 2 && (n - side.size) >= 2)
        .distinct

    def buildNode(members: Set[Int]): NewickTree =
      if (members.size == 1)
        NewickTree(Some(taxa(members.head)), Vector.empty)
      else {
        val inside  = clusters.filter(c => c.size < members.size && c.subsetOf(members))
        val maximal = inside.filter(c => !inside.exists(d => d != c && c.subsetOf(d)))
        val covered = maximal.foldLeft(Set.empty[Int])(_ ++ _)

        val clusterChildren = maximal.map(c => (c.min, buildNode(c)))
        val leafChildren =
          (members -- covered).toVector.map(i => (i, NewickTree(Some(taxa(i)), Vector.empty)))

        val ordered = (clusterChildren ++ leafChildren).sortBy(_._1).map(_._2)
        NewickTree(None, ordered)
      }

    buildNode((0 until n).toSet)
  }

  /** The `'1'`-indices of a character row, complemented if they include the
    * reference (index `0`), so the returned side never contains the reference.
    */
  private def orientAwayFromReference(row: String): Set[Int] = {
    val ones = row.indices.filter(row(_) == '1').toSet
    if (ones.contains(0)) row.indices.toSet.diff(ones) else ones
  }
}
