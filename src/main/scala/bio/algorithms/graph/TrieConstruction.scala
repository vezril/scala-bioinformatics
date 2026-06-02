package bio.algorithms.graph

import bio.domain.graph.{PatternTrie, PatternTrieProblem, TrieEdge}
import bio.domain.nucleic.{DnaNucleotide, DnaString}

/** Constructs the trie encoding a collection of DNA patterns — Rosalind TRIE
  * ("Introduction to Pattern Matching").
  *
  * The root is labelled `1`; new nodes receive successive integers (`2, 3, …`) in
  * the order their edges are created as patterns are inserted in input order.
  * Inserting a pattern walks from the root symbol-by-symbol: when the current node
  * already has a child edge for the symbol the walk reuses it, otherwise a new node
  * and edge are created.
  *
  * Pure and total: construction folds an immutable [[State]] over the patterns (and,
  * within each, over its nucleotides via `DnaNucleotide.fromChar`) — no `var`,
  * `while`, or mutable collection, and no raw `Char` dispatch.
  */
object TrieConstruction {

  private val Root: Int = 1

  def construct(problem: PatternTrieProblem): PatternTrie = {
    val initial = State(nextId = Root + 1, children = Map.empty, edges = Vector.empty)
    val finalState = problem.patterns.foldLeft(initial)(insert)
    PatternTrie(finalState.edges)
  }

  /** Insert one pattern, threading and returning the updated construction state. */
  private def insert(state: State, pattern: DnaString): State =
    pattern.value.iterator
      .flatMap(DnaNucleotide.fromChar)
      .foldLeft((state, Root)) { case ((st, current), symbol) =>
        st.children.get((current, symbol)) match {
          case Some(childId) => (st, childId)
          case None =>
            val childId = st.nextId
            (
              st.copy(
                nextId = st.nextId + 1,
                children = st.children + ((current, symbol) -> childId),
                edges = st.edges :+ TrieEdge(current, childId, symbol)
              ),
              childId
            )
        }
      }
      ._1

  /** Immutable construction state: the next node id to assign, the existing edges
    * keyed by `(parent, symbol)` for O(1) reuse lookup, and the edges in creation
    * order.
    */
  private final case class State(
      nextId: Int,
      children: Map[(Int, DnaNucleotide), Int],
      edges: Vector[TrieEdge]
  )
}
