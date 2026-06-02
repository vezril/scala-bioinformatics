package bio.algorithms.graph

import bio.domain.graph.{SuffixTreeEncoding, SuffixTreeProblem}

import scala.annotation.tailrec

/** Constructs the suffix tree of `s$` and returns the substrings of `s$` labelling its
  * edges — Rosalind SUFF ("Encoding Suffix Trees").
  *
  * Builds the (compressed) suffix tree by naive insertion: each suffix of `text = s$`
  * is walked down from the root; a missing child edge becomes a new leaf, and a
  * partial match splits the existing edge at the divergence point (a new internal node
  * re-parents the old child and a new leaf is attached). Because `text` ends in the
  * unique terminator `$`, every suffix is distinct and none is a prefix of another, so
  * each insertion ends by creating a leaf and never over-runs the text. The suffix tree
  * is unique, so the returned edge-label multiset is independent of insertion/traversal
  * order.
  *
  * Pure and stack-safe: construction folds over the suffixes threading an immutable
  * `Tree`; each insertion is a `@tailrec` descent and the prefix match a `@tailrec`
  * counter. No `var`, `while`, or mutable collection.
  */
object SuffixTreeConstruction {

  private val Root: Int = 0

  /** An edge to `child`, labelled `text.substring(start, end)`. */
  private final case class Edge(start: Int, end: Int, child: Int)

  /** Out-edges keyed by first character, plus the next free node id. */
  private final case class Tree(children: Map[Int, Map[Char, Edge]], nextId: Int)

  def encode(problem: SuffixTreeProblem): SuffixTreeEncoding = {
    val text = problem.dna.value + "$"
    val n    = text.length

    val initial = Tree(children = Map(Root -> Map.empty[Char, Edge]), nextId = 1)
    val tree    = (0 until n).foldLeft(initial)((t, i) => insert(t, text, i, n))

    val labels = tree.children.values.flatMap(_.values).map(e => text.substring(e.start, e.end)).toVector
    SuffixTreeEncoding(labels)
  }

  /** Insert the suffix `text[suffixStart..n)` into the tree. */
  private def insert(tree: Tree, text: String, suffixStart: Int, n: Int): Tree = {
    @tailrec
    def loop(node: Int, pos: Int, t: Tree): Tree = {
      val c = text(pos)
      t.children(node).get(c) match {
        case None =>
          // No matching edge: attach a leaf labelled the rest of the suffix.
          val leaf = t.nextId
          t.copy(
            children = t.children
              .updated(node, t.children(node) + (c -> Edge(pos, n, leaf)))
              .updated(leaf, Map.empty[Char, Edge]),
            nextId = t.nextId + 1
          )
        case Some(edge) =>
          val edgeLen = edge.end - edge.start
          val m       = commonPrefix(text, pos, edge.start, edge.end, n)
          if (m == edgeLen) loop(edge.child, pos + edgeLen, t)
          else {
            // Divergence inside the edge: split at offset m.
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
              nextId = t.nextId + 2
            )
          }
      }
    }
    loop(Root, suffixStart, tree)
  }

  /** Length of the common prefix of `text[pos..n)` and `text[eStart..eEnd)`. */
  private def commonPrefix(text: String, pos: Int, eStart: Int, eEnd: Int, n: Int): Int = {
    @tailrec
    def go(i: Int): Int =
      if (eStart + i < eEnd && pos + i < n && text(pos + i) == text(eStart + i)) go(i + 1)
      else i
    go(0)
  }
}
