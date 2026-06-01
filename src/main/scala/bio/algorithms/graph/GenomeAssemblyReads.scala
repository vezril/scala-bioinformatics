package bio.algorithms.graph

import bio.algorithms.nucleic.DnaReverseComplement
import bio.domain.graph.{CyclicSuperstring, GenomeAssemblyReadsProblem}

import scala.annotation.tailrec

/** Reconstructs a circular chromosome for the Rosalind GASM problem ("Genome
  * Assembly Using Reads").
  *
  * The reads are sampled from either strand of a circular chromosome, so the
  * algorithm first augments the read set with each read's reverse complement
  * (`allReads = S ∪ S^rc`). For the correct k-mer size, the de Bruijn graph over
  * `allReads` — nodes are the distinct length-`(size-1)` k-mers, each length-`size`
  * k-mer contributes the edge `prefix -> suffix` — decomposes into exactly two
  * directed cycles that are reverse complements of one another.
  *
  * Candidate sizes are searched from the read length `L` downward. The largest
  * size whose de Bruijn graph is exactly two simple directed cycles corresponds to
  * the tightest (minimal-length) perfect assembly; smaller sizes over-collapse
  * repeats. A graph is "two simple cycles" iff every node has in-degree =
  * out-degree = 1 (so the adjacency is a bijection on nodes) and walking that
  * permutation decomposes the nodes into exactly two cycles.
  *
  * Each cycle yields a circular string by emitting the first symbol of every node
  * around the cycle (consecutive nodes overlap in `size-2` symbols, so each step
  * contributes one new symbol). The two cycles are reverse complements, so either
  * is a valid Rosalind answer. To be deterministic, the output is the
  * lexicographically smallest minimal rotation across the two cycles. For the
  * canonical sample this is `AATCTGT` — the reverse-complement strand (and a
  * rotation) of the Rosalind sample output `GATTACA`.
  */
object GenomeAssemblyReads {

  def assemble(problem: GenomeAssemblyReadsProblem): CyclicSuperstring = {
    val reads: Set[String] = problem.reads.iterator.map(_.value).toSet
    val allReads: Set[String] =
      reads ++ problem.reads.iterator
        .map(r => DnaReverseComplement.reverseComplement(r).value)

    val length = problem.reads.head.value.length

    val chromosome =
      Iterator
        .range(length, 1, -1)
        .flatMap(size => twoCycleChromosome(allReads, size))
        .nextOption()
        .getOrElse(allReads.min) // unreachable for valid GASM datasets

    CyclicSuperstring(chromosome)
  }

  /** If the de Bruijn graph over the `size`-mers of `allReads` is exactly two
    * simple directed cycles, returns the lexicographically smallest minimal
    * rotation across the two reconstructed circular strings; otherwise `None`.
    */
  private def twoCycleChromosome(allReads: Set[String], size: Int): Option[String] = {
    val kmers: Set[String] =
      allReads.iterator.flatMap(_.sliding(size)).filter(_.length == size).toSet

    if (kmers.isEmpty) None
    else {
      val edges: Set[(String, String)] =
        kmers.map(k => (k.dropRight(1), k.tail))
      val nodes: Set[String] = edges.flatMap { case (a, b) => Set(a, b) }
      val outDeg: Map[String, Int] = edges.groupBy(_._1).map { case (k, v) => k -> v.size }
      val inDeg: Map[String, Int]  = edges.groupBy(_._2).map { case (k, v) => k -> v.size }

      val balanced =
        nodes.forall(n => outDeg.getOrElse(n, 0) == 1 && inDeg.getOrElse(n, 0) == 1)

      if (!balanced) None
      else {
        val adjacency: Map[String, String] = edges.toMap
        val components = cycles(adjacency)
        if (components.size != 2) None
        else
          Some(components.map(c => minimalRotation(c.map(_.head).mkString)).min)
      }
    }
  }

  /** Decomposes a bijective adjacency (every node in/out degree 1) into its
    * disjoint directed cycles, each as the ordered list of its nodes.
    */
  private def cycles(adjacency: Map[String, String]): List[List[String]] = {
    @tailrec
    def walk(start: String, current: String, acc: List[String]): List[String] = {
      val acc2 = current :: acc
      val next = adjacency(current)
      if (next == start) acc2.reverse
      else walk(start, next, acc2)
    }

    @tailrec
    def loop(remaining: Set[String], acc: List[List[String]]): List[List[String]] =
      remaining.headOption match {
        case None => acc
        case Some(start) =>
          val cycle = walk(start, start, Nil)
          loop(remaining -- cycle, cycle :: acc)
      }

    loop(adjacency.keySet, Nil)
  }

  /** The lexicographically smallest rotation of a (circular) string. */
  private def minimalRotation(s: String): String =
    if (s.isEmpty) s
    else s.indices.iterator.map(i => s.substring(i) + s.substring(0, i)).min
}
