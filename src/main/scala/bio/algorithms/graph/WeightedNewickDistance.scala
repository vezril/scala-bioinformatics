package bio.algorithms.graph

import bio.domain.graph.{WeightedNewickTree, WeightedTreeDistanceProblem}

/** Computes the distance between two nodes of a weighted Newick tree — Rosalind NKEW
  * ("Newick Format with Edge Weights").
  *
  * The distance is the sum of edge weights along the unique path connecting the two
  * nodes. The tree is indexed (each node assigned an integer id, with a label→id map and
  * parent→child weighted edges) by a pure depth-first traversal, then converted to an
  * undirected weighted adjacency map; the path weight is found by a DFS from `x` to `y`
  * (the tree guarantees a unique path).
  *
  * Pure and total: recursion threading immutable counters and maps; no `var`, `while`,
  * or mutable collection. Returns a bare `Double` (mirroring NWCK's `NewickDistance`).
  */
object WeightedNewickDistance {

  def between(problem: WeightedTreeDistanceProblem): Double = {
    val (_, _, edges, labelToId) = index(problem.tree, 0, Vector.empty, Map.empty)
    val adjacency                = adjacencyOf(edges)
    pathWeight(adjacency, labelToId(problem.x), labelToId(problem.y))
  }

  /** Assign ids to every node, collecting parent→child weighted edges and a label→id map. */
  private def index(
      tree: WeightedNewickTree,
      nextId: Int,
      edges: Vector[(Int, Int, Double)],
      labels: Map[String, Int]
  ): (Int, Int, Vector[(Int, Int, Double)], Map[String, Int]) = {
    val thisId  = nextId
    val labels1 = tree.label.fold(labels)(l => labels + (l -> thisId))
    val (finalNext, finalEdges, finalLabels) =
      tree.children.foldLeft((nextId + 1, edges, labels1)) {
        case ((nx, es, ls), child) =>
          val (childId, nx2, es2, ls2) = index(child.subtree, nx, es, ls)
          (nx2, es2 :+ ((thisId, childId, child.weight)), ls2)
      }
    (thisId, finalNext, finalEdges, finalLabels)
  }

  /** Undirected weighted adjacency from the directed parent→child edges. */
  private def adjacencyOf(edges: Vector[(Int, Int, Double)]): Map[Int, List[(Int, Double)]] =
    edges.foldLeft(Map.empty[Int, List[(Int, Double)]]) { case (m, (a, b, w)) =>
      m.updated(a, (b, w) :: m.getOrElse(a, Nil))
       .updated(b, (a, w) :: m.getOrElse(b, Nil))
    }

  /** Sum of edge weights on the unique path from `from` to `to`. */
  private def pathWeight(adjacency: Map[Int, List[(Int, Double)]], from: Int, to: Int): Double = {
    def search(current: Int, visited: Set[Int], acc: Double): Option[Double] =
      if (current == to) Some(acc)
      else
        adjacency
          .getOrElse(current, Nil)
          .iterator
          .filterNot { case (nb, _) => visited.contains(nb) }
          .flatMap { case (nb, w) => search(nb, visited + current, acc + w) }
          .nextOption()

    search(from, Set.empty, 0.0).getOrElse(0.0)
  }
}
