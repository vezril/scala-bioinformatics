package bio.algorithms.graph

import bio.domain.graph.{GenomeAssemblyProblem, ShortestSuperstring}

/** Assembles a collection of DNA reads into the shortest superstring containing
  * every read (Rosalind LONG — "Genome Assembly as Shortest Superstring").
  *
  * The general shortest-common-superstring problem is NP-hard, but Rosalind
  * guarantees the reads reconstruct *uniquely* by gluing pairs that overlap by
  * **more than half their length**. Under that guarantee each read has at most one
  * qualifying successor (a read whose prefix it overlaps) and at most one
  * predecessor, so the reads form a single chain — a Hamiltonian path through a
  * sparse overlap graph. Walking that chain and merging on each overlap yields the
  * unique shortest superstring.
  *
  * **Algorithm:**
  *   1. For every ordered pair `(i, j)` compute the maximal overlap: the largest
  *      `k` where `read_i`'s length-`k` suffix equals `read_j`'s length-`k` prefix,
  *      accepting it only when `2k > min(|read_i|, |read_j|)` (the >half rule).
  *   2. If any read has more than one qualifying successor or predecessor the
  *      assembly is ambiguous — return `None`.
  *   3. The start read is the unique read with no predecessor; if there is not
  *      exactly one, return `None`.
  *   4. Walk successors from the start, appending only the non-overlapping tail of
  *      each next read. If the walk revisits a read (cycle) or fails to cover every
  *      read (disconnected), return `None`.
  *
  * A pure function. **Complexity:** `O(n² · L)` to compute overlaps (`n` reads,
  * `L` max read length), then `O(n · L)` to build the superstring.
  */
object GenomeAssembly {

  def assemble(problem: GenomeAssemblyProblem): Option[ShortestSuperstring] = {
    val reads = problem.reads.map(_.value)
    val n     = reads.size

    if (n == 1) Some(ShortestSuperstring(reads.head))
    else {
      val indices = reads.indices

      val successors: Map[Int, Vector[Int]] =
        indices
          .map(i => i -> indices.filter(j => j != i && overlap(reads(i), reads(j)) > 0).toVector)
          .toMap

      val ambiguousSuccessor = successors.exists { case (_, js) => js.size > 1 }
      val succ: Map[Int, Int] = successors.collect { case (i, Vector(j)) => i -> j }
      val predecessorCounts: Map[Int, Int] =
        succ.values.groupBy(identity).map { case (j, occ) => j -> occ.size }
      val ambiguousPredecessor = predecessorCounts.exists { case (_, c) => c > 1 }
      val starts               = indices.filterNot(predecessorCounts.contains)

      if (ambiguousSuccessor || ambiguousPredecessor || starts.size != 1) None
      else
        chainFrom(starts.head, succ)
          .filter(_.size == n)
          .map(chain => ShortestSuperstring(merge(chain.map(reads))))
    }
  }

  /** Follows the unique-successor links from `start`, returning the ordered chain
    * of read indices, or `None` if a cycle is encountered.
    */
  private def chainFrom(start: Int, succ: Map[Int, Int]): Option[List[Int]] = {
    @annotation.tailrec
    def loop(cur: Int, acc: List[Int], visited: Set[Int]): Option[List[Int]] =
      if (visited.contains(cur)) None
      else
        succ.get(cur) match {
          case None      => Some((cur :: acc).reverse)
          case Some(nxt) => loop(nxt, cur :: acc, visited + cur)
        }
    loop(start, Nil, Set.empty)
  }

  /** Concatenates reads already in chain order, dropping each read's overlapping
    * prefix against the read before it.
    */
  private def merge(ordered: List[String]): String =
    ordered match {
      case Nil => ""
      case head :: tail =>
        tail
          .foldLeft((head, head)) { case ((acc, prev), cur) =>
            (acc + cur.drop(overlap(prev, cur)), cur)
          }
          ._1
    }

  /** The qualifying overlap of `a` into `b`: the largest `k` where `a`'s length-`k`
    * suffix equals `b`'s length-`k` prefix, returned only when it exceeds half the
    * shorter read's length; otherwise `0`.
    */
  private def overlap(a: String, b: String): Int = {
    val maxK = math.min(a.length, b.length)
    val best = (maxK to 1 by -1).find(k => a.takeRight(k) == b.take(k)).getOrElse(0)
    if (best > 0 && 2 * best > maxK) best else 0
  }
}
