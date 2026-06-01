package bio.algorithms.graph

import bio.domain.graph.{CyclicSuperstring, PerfectCoverageProblem}

/** Reconstructs a circular chromosome for the Rosalind PCOV problem ("Genome
  * Assembly with Perfect Coverage").
  *
  * For reads of length `L`, the de Bruijn graph has the distinct length-`(L-1)`
  * k-mers as nodes; each read `r` contributes the directed edge
  * `r.dropRight(1) -> r.tail` (its length-`(L-1)` prefix to its length-`(L-1)`
  * suffix). The dataset guarantees this graph is exactly one simple cycle, so
  * every node has in-degree = out-degree = 1 and a `Map[String, String]` is a
  * faithful, total adjacency (a bijection on nodes).
  *
  * Reads are de-duplicated structurally by collecting their values into a
  * `Set[String]` first, so repeated reads cannot distort the cycle.
  *
  * The cycle is walked starting from the lexicographically smallest node,
  * emitting the first symbol of each visited node. Consecutive nodes overlap in
  * `L-2` symbols, so each step contributes exactly one new symbol; collecting one
  * symbol per node around the full cycle reproduces the circular string, whose
  * length equals the node count. The deterministic start makes the output a fixed
  * rotation of the chromosome — Rosalind accepts any rotation as correct.
  */
object PerfectCoverageAssembly {

  def assemble(problem: PerfectCoverageProblem): CyclicSuperstring = {
    val adjacency: Map[String, String] =
      problem.kmers.iterator
        .map(_.value)
        .toSet
        .map((r: String) => r.dropRight(1) -> r.tail)
        .toMap

    val start = adjacency.keysIterator.min
    val chromosome =
      Iterator
        .iterate(start)(adjacency)
        .take(adjacency.size)
        .map(_.head)
        .mkString

    CyclicSuperstring(chromosome)
  }
}
