package bio.algorithms.graph

import bio.algorithms.nucleic.DnaReverseComplement
import bio.domain.graph.{DeBruijnEdge, DeBruijnGraph, DeBruijnGraphProblem}

/** Constructs the de Bruijn graph `B_k` for the Rosalind DBRU problem.
  *
  * Given a validated [[DeBruijnGraphProblem]] of (k+1)-mers `S`, the graph is
  * `B_k` over `S ∪ S^rc`, where `S^rc` is the set of reverse complements of `S`.
  * For each distinct (k+1)-mer `r` the graph has exactly one edge from `r`'s
  * length-`k` prefix to its length-`k` suffix.
  *
  * De-duplication is structural: the (k+1)-mer values are collected into a
  * `Set[String]`, unioned with the set of their reverse complements, so
  * duplicated inputs and reverse-complement palindromes (an `r` equal to its own
  * reverse complement) collapse to a single element — and therefore a single
  * edge — automatically. The edges are then sorted lexicographically by
  * `(from, to)` for deterministic, Rosalind-matching output.
  */
object DeBruijnGraphConstruction {

  def construct(problem: DeBruijnGraphProblem): DeBruijnGraph = {
    val forward: Set[String] = problem.kmers.iterator.map(_.value).toSet
    val reverse: Set[String] =
      problem.kmers.iterator
        .map(k => DnaReverseComplement.reverseComplement(k).value)
        .toSet

    val edges = (forward union reverse).toVector
      .map(r => DeBruijnEdge(r.dropRight(1), r.tail))
      .sortBy(e => (e.from, e.to))

    DeBruijnGraph(edges)
  }
}
