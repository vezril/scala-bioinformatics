package bio.algorithms.graph

import bio.domain.graph.{NewickTree, UnrootedBinaryTrees, UnrootedBinaryTreesProblem}

/** Enumerates every unrooted binary tree on a set of taxa — Rosalind EUBT
  * ("Enumerating Unrooted Binary Trees").
  *
  * Every tree is rooted at the first taxon: the structure below the root is a binary
  * tree whose leaves are the remaining taxa. Starting from the unique tree on the first
  * three taxa, each subsequent taxon is inserted onto every edge (subdividing the edge
  * with a new internal node). A tree whose body has `N` nodes has `N` edges, so each
  * insertion yields `N` new trees; the total is `(2n−5)!!`.
  *
  * Pure and total: enumeration is recursion + `flatMap` over immutable structures (no
  * `var`, `while`, or mutable collection). Each tree is rendered with the canonical
  * [[NewickTree]] renderer, producing the `(body)root;` shape.
  */
object EnumerateUnrootedBinaryTrees {

  /** Binary tree below the root: leaves are taxa, internal nodes are unlabelled. */
  private sealed trait BinaryTree
  private final case class Leaf(name: String) extends BinaryTree
  private final case class Node(left: BinaryTree, right: BinaryTree) extends BinaryTree

  def enumerate(problem: UnrootedBinaryTreesProblem): UnrootedBinaryTrees = {
    val taxa = problem.taxa
    val root = taxa(0)
    val base = Node(Leaf(taxa(1)), Leaf(taxa(2)))

    val bodies =
      taxa.drop(3).foldLeft(List[BinaryTree](base)) { (trees, taxon) =>
        trees.flatMap(insertions(_, taxon))
      }

    UnrootedBinaryTrees(bodies.iterator.map(render(_, root)).toVector)
  }

  /** All trees obtained by subdividing each edge of `t` with a new leaf `x` — one per
    * node of `t` (the edge above that node).
    */
  private def insertions(t: BinaryTree, x: String): List[BinaryTree] =
    t match {
      case Leaf(_) =>
        List(Node(t, Leaf(x)))
      case Node(l, r) =>
        Node(t, Leaf(x)) ::
          (insertions(l, x).map(Node(_, r)) ++ insertions(r, x).map(Node(l, _)))
    }

  /** Render the unrooted tree as `(body)root;` via the canonical Newick renderer. */
  private def render(body: BinaryTree, root: String): String =
    NewickTree(Some(root), Vector(toNewick(body))).render

  private def toNewick(t: BinaryTree): NewickTree =
    t match {
      case Leaf(name)  => NewickTree(Some(name), Vector.empty)
      case Node(l, r)  => NewickTree(None, Vector(toNewick(l), toNewick(r)))
    }
}
