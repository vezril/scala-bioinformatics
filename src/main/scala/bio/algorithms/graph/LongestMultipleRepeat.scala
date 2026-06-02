package bio.algorithms.graph

import bio.domain.graph.{LongestRepeat, LongestRepeatProblem, SuffixTreeEdge}

import scala.annotation.tailrec

/** Finds the longest substring of `s` occurring at least `k` times, given the suffix
  * tree of `s$` — Rosalind LREP ("Finding the Longest Multiple Repeat").
  *
  * In a suffix tree the number of leaves beneath a node equals the number of times the
  * substring spelled from the root to that node occurs in `s`. The answer is therefore
  * the path-string of the string-deepest internal node whose subtree has at least `k`
  * leaves (the empty string if none qualifies). Internal nodes have `$`-free
  * path-strings, so the answer is always a genuine substring of `s`.
  *
  * Stack-safe and pure: depths are computed by a `@tailrec` breadth-first sweep,
  * leaf-counts by a `foldLeft` over nodes in decreasing depth (each child contributes
  * before its parent), and the winning path-string by a `@tailrec` walk up the parent
  * edges. No `var`, `while`, or mutable collection.
  */
object LongestMultipleRepeat {

  def find(problem: LongestRepeatProblem): LongestRepeat = {
    val edges      = problem.edges
    val childrenOf = edges.groupBy(_.parent)
    val parentEdge = edges.iterator.map(e => e.child -> e).toMap
    val nodes      = (edges.iterator.map(_.parent) ++ edges.iterator.map(_.child)).toVector.distinct

    if (nodes.isEmpty) LongestRepeat("")
    else {
      val root        = nodes.find(n => !parentEdge.contains(n)).getOrElse(nodes.head)
      val edgeDepth   = depths(root, childrenOf)
      val stringDepth = stringDepths(root, childrenOf)
      val leafCount   = leafCounts(nodes, childrenOf, parentEdge, edgeDepth)

      val winner =
        nodes.iterator
          .filter(n => childrenOf.contains(n) && leafCount.getOrElse(n, 0) >= problem.k)
          .maxByOption(n => stringDepth.getOrElse(n, 0))

      LongestRepeat(winner.map(pathString(_, parentEdge, problem.text)).getOrElse(""))
    }
  }

  /** Edge-distance from the root, by a tail-recursive breadth-first sweep. */
  private def depths(root: String, childrenOf: Map[String, Vector[SuffixTreeEdge]]): Map[String, Int] = {
    @tailrec
    def loop(frontier: Vector[String], d: Int, acc: Map[String, Int]): Map[String, Int] =
      if (frontier.isEmpty) acc
      else {
        val children = frontier.flatMap(n => childrenOf.getOrElse(n, Vector.empty).map(_.child))
        loop(children, d + 1, acc ++ children.map(_ -> (d + 1)))
      }
    loop(Vector(root), 0, Map(root -> 0))
  }

  /** Character-depth (concatenated edge-label length) from the root, top-down. */
  private def stringDepths(
      root: String,
      childrenOf: Map[String, Vector[SuffixTreeEdge]]
  ): Map[String, Int] = {
    @tailrec
    def loop(frontier: Vector[String], acc: Map[String, Int]): Map[String, Int] =
      if (frontier.isEmpty) acc
      else {
        val childEdges = frontier.flatMap(n => childrenOf.getOrElse(n, Vector.empty))
        val next = childEdges.foldLeft(acc) { (m, e) =>
          m + (e.child -> (m.getOrElse(e.parent, 0) + e.length))
        }
        loop(childEdges.map(_.child), next)
      }
    loop(Vector(root), Map(root -> 0))
  }

  /** Subtree leaf-counts: leaves start at 1, internal nodes at 0; processing nodes in
    * decreasing edge-depth, each node's count is added to its parent's (children are
    * always deeper, so contribute first).
    */
  private def leafCounts(
      nodes: Vector[String],
      childrenOf: Map[String, Vector[SuffixTreeEdge]],
      parentEdge: Map[String, SuffixTreeEdge],
      edgeDepth: Map[String, Int]
  ): Map[String, Int] = {
    val initial = nodes.map(n => n -> (if (childrenOf.contains(n)) 0 else 1)).toMap
    nodes
      .sortBy(n => -edgeDepth.getOrElse(n, 0))
      .foldLeft(initial) { (counts, node) =>
        parentEdge.get(node) match {
          case Some(e) => counts.updated(e.parent, counts(e.parent) + counts(node))
          case None    => counts
        }
      }
  }

  /** The path-string from the root to `node`, reconstructed by a tail-recursive walk up
    * the parent edges (labels collected then joined).
    */
  private def pathString(node: String, parentEdge: Map[String, SuffixTreeEdge], text: String): String = {
    @tailrec
    def loop(current: String, acc: List[String]): List[String] =
      parentEdge.get(current) match {
        case None => acc
        case Some(e) =>
          loop(e.parent, text.substring(e.start - 1, e.start - 1 + e.length) :: acc)
      }
    loop(node, Nil).mkString
  }
}
