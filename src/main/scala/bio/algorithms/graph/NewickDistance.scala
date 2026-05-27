package bio.algorithms.graph

import bio.domain.graph.{NewickDistanceProblem, NewickTree}

import scala.annotation.tailrec
import scala.collection.immutable.Queue

/** Computes the number of edges on the unique path between two labelled nodes
  * of a parsed Newick tree (Rosalind NWCK — "Distances in Trees").
  *
  * **Algorithm:**
  *   1. Walk the tree in pre-order, assigning each node a synthetic integer ID
  *      and recording two structures:
  *        - `adj: Map[Int, Vector[Int]]` — undirected adjacency keyed by ID
  *          (every parent↔child link is registered in both directions).
  *        - `labelToId: Map[String, Int]` — first occurrence of each label
  *          (Newick does not guarantee unique labels, so we record the first).
  *   2. Look up the source and target IDs in `labelToId`.
  *   3. Short-circuit `x == y` to return `0`.
  *   4. BFS from the source ID; the first time the target ID is dequeued, its
  *      recorded depth is the answer.
  *
  * **Why synthetic IDs.** Newick allows unlabelled internal nodes (e.g. the root
  * of `(dog,cat);`), so a label-keyed adjacency map could not even represent
  * the path. Synthetic IDs sidestep both that problem and any future duplicate-
  * label concern.
  *
  * **Complexity:** O(V + E) per query. Rosalind caps trees at 200 nodes, so each
  * call is microseconds.
  */
object NewickDistance {

  def between(problem: NewickDistanceProblem): Int = {
    val (adj, labelToId) = build(problem.tree)
    val sourceId         = labelToId(problem.x)
    val targetId         = labelToId(problem.y)
    if (sourceId == targetId) 0
    else bfs(adj, sourceId, targetId)
  }

  /** Walks the tree once, returning the undirected adjacency map (keyed by
    * synthetic int IDs) and a label→first-occurrence-ID lookup table. Pure-
    * functional: state is threaded through the recursive `visit` rather than
    * mutated.
    */
  private def build(tree: NewickTree): (Map[Int, Vector[Int]], Map[String, Int]) = {
    def visit(
        node: NewickTree,
        parentId: Option[Int],
        adj: Map[Int, Vector[Int]],
        labelToId: Map[String, Int],
        nextId: Int
    ): (Map[Int, Vector[Int]], Map[String, Int], Int) = {
      val myId          = nextId
      val adjWithMe     = adj.updated(myId, Vector.empty[Int])
      val labelToIdNext = node.label match {
        case Some(lbl) if !labelToId.contains(lbl) => labelToId.updated(lbl, myId)
        case _                                     => labelToId
      }
      val adjWithEdge = parentId match {
        case None => adjWithMe
        case Some(p) =>
          adjWithMe
            .updated(p, adjWithMe.getOrElse(p, Vector.empty) :+ myId)
            .updated(myId, adjWithMe.getOrElse(myId, Vector.empty) :+ p)
      }
      node.children.foldLeft((adjWithEdge, labelToIdNext, nextId + 1)) {
        case ((a, l, n), child) => visit(child, Some(myId), a, l, n)
      }
    }

    val (adj, labelToId, _) = visit(tree, None, Map.empty, Map.empty, 0)
    (adj, labelToId)
  }

  private def bfs(adj: Map[Int, Vector[Int]], source: Int, target: Int): Int = {
    @tailrec
    def loop(queue: Queue[(Int, Int)], visited: Set[Int]): Int =
      queue.dequeueOption match {
        case None => -1 // unreachable in a connected tree, but guards the type
        case Some(((nodeId, dist), rest)) =>
          if (nodeId == target) dist
          else {
            val neighbours = adj.getOrElse(nodeId, Vector.empty).filterNot(visited)
            val enqueued   = neighbours.foldLeft(rest)((q, n) => q.enqueue((n, dist + 1)))
            loop(enqueued, visited ++ neighbours)
          }
      }

    loop(Queue((source, 0)), Set(source))
  }
}
