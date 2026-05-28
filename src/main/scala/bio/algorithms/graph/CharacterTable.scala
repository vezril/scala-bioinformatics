package bio.algorithms.graph

import bio.domain.graph.{CharacterTableProblem, NewickTree}

/** Builds the character table of an unrooted binary tree (Rosalind CSTR —
  * "Creating a Character Table").
  *
  * Each *internal* edge of an unrooted binary tree, when removed, partitions
  * the leaves into two non-empty disjoint subsets `S | S^c` — a *split*. The
  * character table has one row per **nontrivial** split (where both sides
  * contain `≥ 2` taxa), with `n` columns indexed by `problem.leafLabels`
  * (lexicographically sorted). Each row encodes which side of the split each
  * taxon belongs to.
  *
  * **Algorithm:**
  *   1. Walk every non-root node of `problem.tree`, recording the set of
  *      labelled leaves in its subtree. Every non-root node contributes one
  *      split (the edge *above* the node — its parent edge — is the one being
  *      removed).
  *   2. Filter out trivial splits — sides with `< 2` leaves on either side.
  *   3. For each retained split, emit a row using the **"lex-first taxon's
  *      side gets `0`s"** convention: position `i` is `'1'` iff
  *      `problem.leafLabels(i)` is in the side that does NOT contain
  *      `problem.leafLabels.head`. This is a deterministic choice from the
  *      two equivalent encodings the Rosalind spec allows, and reproduces the
  *      sample answer.
  *   4. Deduplicate and lex-sort the rows for stable output. Distinct edges
  *      in a tree produce distinct splits, but the same split may be derived
  *      from both endpoints during the walk, so `.distinct` is required.
  *
  * **Empty result** when fewer than 4 taxa exist (no nontrivial split is
  * possible) or when every edge is a leaf-edge (e.g. a flat star).
  *
  * **Complexity:** `O(n · e)` where `n` is the leaf count and `e` is the
  * number of internal edges. At Rosalind's cap of 200 taxa the runtime is
  * microseconds — no need for parent-pointer caching.
  */
object CharacterTable {

  def compute(problem: CharacterTableProblem): Vector[String] = {
    val taxa = problem.leafLabels
    val n    = taxa.size
    if (n < 4) Vector.empty
    else {
      val firstTaxon = taxa.head
      val splits     = enumerateSplits(problem.tree)
      splits
        .filter(s => s.size >= 2 && (n - s.size) >= 2)
        .map(s => encode(s, taxa, firstTaxon))
        .distinct
        .sorted
    }
  }

  /** Collects, for every *non-root* node of the tree, the set of labelled
    * leaves in its subtree. Each entry corresponds to one tree edge (the
    * edge from the node to its parent).
    */
  private def enumerateSplits(root: NewickTree): Vector[Set[String]] = {
    def collectLeaves(node: NewickTree): Set[String] =
      if (node.children.isEmpty) node.label.toSet
      else node.children.foldLeft(Set.empty[String])(_ ++ collectLeaves(_))

    def walk(node: NewickTree, isRoot: Boolean): Vector[Set[String]] = {
      val here = if (isRoot) Vector.empty else Vector(collectLeaves(node))
      here ++ node.children.flatMap(child => walk(child, isRoot = false))
    }

    walk(root, isRoot = true)
  }

  /** Encodes a split as a `0`/`1` string of length `taxa.size`. The side
    * containing `firstTaxon` gets `0`s; the other side gets `1`s. Equivalently:
    * each taxon's bit is `'0'` iff it is on the *same* side as `firstTaxon`.
    */
  private def encode(
      sideA: Set[String],
      taxa: Vector[String],
      firstTaxon: String
  ): String = {
    val firstInA = sideA.contains(firstTaxon)
    taxa.iterator.map(t => if (sideA.contains(t) == firstInA) '0' else '1').mkString
  }
}
