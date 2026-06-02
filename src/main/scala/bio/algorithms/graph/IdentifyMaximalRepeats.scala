package bio.algorithms.graph

import bio.domain.graph.{MaximalRepeatProblem, MaximalRepeats}

import scala.annotation.tailrec

/** Identifies the maximal repeats of a DNA string — Rosalind MREP ("Identifying
  * Maximal Repeats").
  *
  * By Gusfield's theorem, the maximal repeats of `s` are exactly the path-labels of the
  * left-diverse internal nodes of the suffix tree of `text = s + "$"`:
  *   - an internal node (≥ 2 children) is right-maximal — its path-label is followed by
  *     ≥ 2 distinct characters;
  *   - it is left-diverse when the leaves in its subtree are preceded by ≥ 2 distinct
  *     characters (the start-of-string counts as a distinct preceding "character").
  *
  * The suffix tree is built by naive insertion (as in SUFF) augmented to record each
  * leaf's suffix start position. Node analyses (string-depth, per-node preceding-char
  * set, a representative leaf) are computed by a tail-recursive BFS and decreasing-depth
  * folds — pure and stack-safe, with no `var`/`while`/mutable collection.
  */
object IdentifyMaximalRepeats {

  private val Root: Int = 0

  private final case class Edge(start: Int, end: Int, child: Int) {
    def length: Int = end - start
  }

  /** Out-edges keyed by first character, the next free node id, and each leaf's suffix
    * start position.
    */
  private final case class Tree(
      children: Map[Int, Map[Char, Edge]],
      nextId: Int,
      leafStart: Map[Int, Int]
  )

  def find(problem: MaximalRepeatProblem): MaximalRepeats = {
    val text = problem.dna.value + "$"
    val n    = text.length

    val initial = Tree(Map(Root -> Map.empty[Char, Edge]), 1, Map.empty)
    val tree    = (0 until n).foldLeft(initial)((t, i) => insert(t, text, i, n))

    val nodes       = tree.children.keys.toVector
    val parentEdge  = parentEdges(tree.children)
    val depth       = edgeDepths(Root, tree.children)
    val stringDepth = stringDepths(Root, tree.children)
    val (leftChars, repLeafStart) = aggregate(nodes, tree, parentEdge, depth, text)

    val repeats =
      nodes.iterator
        .collect {
          case node
              if tree.children(node).size >= 2 && leftChars(node).size >= 2 =>
            text.substring(repLeafStart(node), repLeafStart(node) + stringDepth(node))
        }
        .filter(_.length >= problem.minLength)
        .toVector
        .distinct
        .sorted

    MaximalRepeats(repeats)
  }

  /** Insert the suffix `text[suffixStart..n)`, recording the new leaf's start position. */
  private def insert(tree: Tree, text: String, suffixStart: Int, n: Int): Tree = {
    @tailrec
    def loop(node: Int, pos: Int, t: Tree): Tree = {
      val c = text(pos)
      t.children(node).get(c) match {
        case None =>
          val leaf = t.nextId
          t.copy(
            children = t.children
              .updated(node, t.children(node) + (c -> Edge(pos, n, leaf)))
              .updated(leaf, Map.empty[Char, Edge]),
            nextId = t.nextId + 1,
            leafStart = t.leafStart + (leaf -> suffixStart)
          )
        case Some(edge) =>
          val edgeLen = edge.end - edge.start
          val m       = commonPrefix(text, pos, edge.start, edge.end, n)
          if (m == edgeLen) loop(edge.child, pos + edgeLen, t)
          else {
            val mid  = t.nextId
            val leaf = t.nextId + 1
            t.copy(
              children = t.children
                .updated(node, t.children(node) + (c -> Edge(edge.start, edge.start + m, mid)))
                .updated(
                  mid,
                  Map(
                    text(edge.start + m) -> Edge(edge.start + m, edge.end, edge.child),
                    text(pos + m)        -> Edge(pos + m, n, leaf)
                  )
                )
                .updated(leaf, Map.empty[Char, Edge]),
              nextId = t.nextId + 2,
              leafStart = t.leafStart + (leaf -> suffixStart)
            )
          }
      }
    }
    loop(Root, suffixStart, tree)
  }

  private def commonPrefix(text: String, pos: Int, eStart: Int, eEnd: Int, n: Int): Int = {
    @tailrec
    def go(m: Int): Int =
      if (eStart + m < eEnd && pos + m < n && text(pos + m) == text(eStart + m)) go(m + 1) else m
    go(0)
  }

  /** child -> (parent, edge) for every edge. */
  private def parentEdges(children: Map[Int, Map[Char, Edge]]): Map[Int, (Int, Edge)] =
    children.iterator.flatMap { case (parent, m) =>
      m.values.iterator.map(e => e.child -> (parent, e))
    }.toMap

  /** Edge-distance from the root, by tail-recursive BFS. */
  private def edgeDepths(root: Int, children: Map[Int, Map[Char, Edge]]): Map[Int, Int] = {
    @tailrec
    def loop(frontier: Vector[Int], d: Int, acc: Map[Int, Int]): Map[Int, Int] =
      if (frontier.isEmpty) acc
      else {
        val next = frontier.flatMap(children(_).values.map(_.child))
        loop(next, d + 1, acc ++ next.map(_ -> (d + 1)))
      }
    loop(Vector(root), 0, Map(root -> 0))
  }

  /** Character-depth (path-label length) from the root, top-down. */
  private def stringDepths(root: Int, children: Map[Int, Map[Char, Edge]]): Map[Int, Int] = {
    @tailrec
    def loop(frontier: Vector[Int], acc: Map[Int, Int]): Map[Int, Int] =
      if (frontier.isEmpty) acc
      else {
        val childPairs = frontier.flatMap(p => children(p).values.map(e => (p, e)))
        val next = childPairs.foldLeft(acc) { case (m, (parent, e)) =>
          m + (e.child -> (m(parent) + e.length))
        }
        loop(childPairs.map(_._2.child), next)
      }
    loop(Vector(root), Map(root -> 0))
  }

  /** Per-node preceding-character set and a representative leaf start, computed by a
    * decreasing-depth fold (children contribute before their parent).
    */
  private def aggregate(
      nodes: Vector[Int],
      tree: Tree,
      parentEdge: Map[Int, (Int, Edge)],
      depth: Map[Int, Int],
      text: String
  ): (Map[Int, Set[Option[Char]]], Map[Int, Int]) = {
    def leftCharOf(start: Int): Option[Char] = if (start == 0) None else Some(text(start - 1))

    val initLeft: Map[Int, Set[Option[Char]]] = nodes.map { node =>
      node -> tree.leafStart.get(node).map(s => Set(leftCharOf(s))).getOrElse(Set.empty[Option[Char]])
    }.toMap
    val initRep: Map[Int, Int] = nodes.map { node =>
      node -> tree.leafStart.getOrElse(node, -1)
    }.toMap

    nodes
      .sortBy(node => -depth(node))
      .foldLeft((initLeft, initRep)) { case ((leftChars, repStart), node) =>
        parentEdge.get(node) match {
          case None => (leftChars, repStart)
          case Some((parent, _)) =>
            val left2 = leftChars.updated(parent, leftChars(parent) ++ leftChars(node))
            val rep2  = if (repStart(parent) == -1) repStart.updated(parent, repStart(node)) else repStart
            (left2, rep2)
        }
      }
  }
}
