package bio.algorithms.graph

import bio.domain.graph.{CompleteCycleAssemblies, CompleteCycleProblem}

/** Enumerates every circular string assembled by a *complete cycle* in the de
  * Bruijn graph of a read collection — Rosalind GREP ("Genome Assembly with Perfect
  * Coverage and Repeats").
  *
  * For reads of length `L = k + 1`, the de Bruijn graph `B_k` has the length-`k`
  * k-mers as nodes; each read `r` contributes the directed edge
  * `r.dropRight(1) -> r.tail` (its length-`k` prefix to its length-`k` suffix).
  * Because `r = from ++ to.last`, an edge `(from, to)` uniquely identifies its read,
  * so **read multiplicity is preserved** by keeping the available edges as a multiset
  * `Map[(from, to), Int]`. (Contrast [[PerfectCoverageAssembly]], which de-duplicates
  * into a `Set` because the perfect-coverage graph is a simple cycle — here repeats
  * are exactly what produce multiple complete cycles.)
  *
  * A *complete cycle* is an Eulerian circuit that traverses each edge exactly its
  * read multiplicity. Every result must begin with the first input read, so the first
  * edge is fixed to it and `start` is that edge's `from` node. The remaining edges are
  * enumerated by pure functional backtracking: from the current node, try each
  * distinct still-available edge, recursing on its `to` node with the multiset
  * decremented; a path is a circuit exactly when all edges are consumed and the walk
  * has returned to `start`. Dead-ending partial paths are discarded automatically.
  *
  * Each circuit `e1, e2, …, eE` (E = read count) assembles to a circular string by
  * taking the first read in full and appending the last symbol of each subsequent
  * edge, then truncating to length `E` (the trailing `k` symbols wrap around). The
  * assembled strings are de-duplicated (distinct circuits can coincide under repeats)
  * and sorted for deterministic output; Rosalind accepts them in any order.
  */
object CompleteCycleAssembly {

  /** A de Bruijn edge keyed by its `(from, to)` length-`k` k-mer nodes. */
  private final case class Edge(from: String, to: String)

  def assemble(problem: CompleteCycleProblem): CompleteCycleAssemblies = {
    val reads = problem.kmers.map(_.value)
    val edges = reads.map(r => Edge(r.dropRight(1), r.tail))

    // Available edges as a multiset, keyed by edge with its read multiplicity.
    val allEdges: Map[Edge, Int] =
      edges.groupBy(identity).map { case (e, group) => e -> group.size }

    val firstRead = reads.head
    val firstEdge = edges.head
    val start     = firstEdge.from

    // Outgoing edges grouped by their source node (distinct edges per node).
    val outgoing: Map[String, Vector[Edge]] =
      allEdges.keysIterator.toVector.groupBy(_.from)

    def decremented(remaining: Map[Edge, Int], e: Edge): Map[Edge, Int] =
      remaining(e) match {
        case 1 => remaining - e
        case n => remaining.updated(e, n - 1)
      }

    // All Eulerian continuations from `node` consuming exactly `remaining`,
    // closing back at `start`. Pure backtracking, no mutable state.
    def extend(node: String, remaining: Map[Edge, Int]): List[List[Edge]] =
      if (remaining.isEmpty)
        if (node == start) List(Nil) else Nil
      else
        outgoing
          .getOrElse(node, Vector.empty)
          .filter(remaining.contains)
          .toList
          .flatMap { e =>
            extend(e.to, decremented(remaining, e)).map(e :: _)
          }

    val circuits: List[List[Edge]] =
      extend(firstEdge.to, decremented(allEdges, firstEdge)).map(firstEdge :: _)

    val length = reads.size
    val strings = circuits
      .map { circuit =>
        val full = firstRead + circuit.tail.map(_.to.last).mkString
        full.take(length)
      }
      .distinct
      .sorted
      .toVector

    CompleteCycleAssemblies(strings)
  }
}
