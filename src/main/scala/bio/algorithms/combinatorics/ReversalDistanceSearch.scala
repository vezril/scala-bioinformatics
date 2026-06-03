package bio.algorithms.combinatorics

import bio.domain.combinatorics.{ReversalDistance, ReversalDistanceProblem}

import scala.collection.mutable

/** Computes the Rosalind REAR ("Reversal Distance") between two permutations:
  * the minimum number of contiguous-interval reversals transforming the source
  * into the target.
  *
  * **Algorithm.** Reversal distance is invariant under relabelling, so the
  * target is normalised to the identity permutation `1..n` (by relabelling the
  * source through the target's inverse). The distance from the relabelled
  * source to the identity is then found by **bidirectional BFS** over the
  * reversal graph: each node a permutation, each of the `C(n,2)` edges a single
  * interval reversal. Two frontiers grow — one from the source, one from the
  * identity — always expanding the side with the smaller current frontier; when
  * a generated neighbour already lives in the *other* side's visited map, the
  * search has met. The answer is the minimum `dSource + dTarget` over the
  * meetings discovered while completing that level (which is optimal because
  * `dSource + dTarget` only grows across levels).
  *
  * Permutations (`n ≤ 10`, values `1..10`) are packed into a `Long` — 4 bits per
  * value — for compact, fast visited-set keys (`mutable.LongMap`). The
  * imperative BFS internals are confined here; the public [[distance]] signature
  * is pure and total.
  */
object ReversalDistanceSearch {

  def distance(problem: ReversalDistanceProblem): ReversalDistance = {
    val source = problem.source.values
    val target = problem.target.values
    val n      = source.length

    if (n <= 1) ReversalDistance(0)
    else {
      val start = relabelToIdentity(source.toArray, target.toArray)
      val goal  = Array.tabulate(n)(i => i + 1)
      ReversalDistance(bidirectional(encode(start), encode(goal), n))
    }
  }

  /** Relabel `source` so that `target` becomes the identity `1..n`, applying the
    * same value→rank map to `source`. Preserves reversal distance.
    */
  private def relabelToIdentity(source: Array[Int], target: Array[Int]): Array[Int] = {
    val rank = new Array[Int](target.length + 1) // value -> 1-based position in target
    var i    = 0
    while (i < target.length) {
      rank(target(i)) = i + 1
      i += 1
    }
    source.map(rank)
  }

  /** Pack a permutation of values `1..10` into a `Long`, 4 bits per element. */
  private def encode(perm: Array[Int]): Long = {
    var key = 0L
    var i   = 0
    while (i < perm.length) {
      key = (key << 4) | perm(i).toLong
      i += 1
    }
    key
  }

  /** Unpack `n` elements of a `Long` key back into an array. */
  private def decode(key: Long, n: Int): Array[Int] = {
    val perm = new Array[Int](n)
    var i    = n - 1
    var k    = key
    while (i >= 0) {
      perm(i) = (k & 0xfL).toInt
      k >>>= 4
      i -= 1
    }
    perm
  }

  /** All single-reversal neighbour keys of `key` (intervals `[i, j]`, `i < j`). */
  private def neighbours(key: Long, n: Int): Array[Long] = {
    val perm = decode(key, n)
    val out  = new Array[Long](n * (n - 1) / 2)
    var idx  = 0
    var i    = 0
    while (i < n) {
      var j = i + 1
      while (j < n) {
        reverse(perm, i, j)
        out(idx) = encode(perm)
        reverse(perm, i, j) // restore
        idx += 1
        j += 1
      }
      i += 1
    }
    out
  }

  private def reverse(perm: Array[Int], from: Int, to: Int): Unit = {
    var a = from
    var b = to
    while (a < b) {
      val t = perm(a); perm(a) = perm(b); perm(b) = t
      a += 1
      b -= 1
    }
  }

  /** Bidirectional BFS returning the shortest reversal distance from `start` to `goal`. */
  private def bidirectional(start: Long, goal: Long, n: Int): Int = {
    if (start == goal) 0
    else {
      val visitedS = mutable.LongMap[Int](start -> 0)
      val visitedG = mutable.LongMap[Int](goal -> 0)
      var frontierS = List(start)
      var frontierG = List(goal)
      var depthS    = 0
      var depthG    = 0
      var answer    = -1

      while (answer < 0) {
        // Expand whichever side currently has the smaller frontier.
        if (frontierS.size <= frontierG.size) {
          val (next, found) = expand(frontierS, visitedS, visitedG, depthS + 1, n)
          depthS = depthS + 1
          frontierS = next
          if (found >= 0) answer = found
          else if (next.isEmpty) answer = Int.MaxValue // unreachable (should not happen)
        } else {
          val (next, found) = expand(frontierG, visitedG, visitedS, depthG + 1, n)
          depthG = depthG + 1
          frontierG = next
          if (found >= 0) answer = found
          else if (next.isEmpty) answer = Int.MaxValue
        }
      }
      answer
    }
  }

  /** Expand one BFS level from `frontier`; record newly-seen states in `ours` at
    * `newDepth`. Returns the next frontier and, if any neighbour is already in
    * `theirs`, the minimum total distance (`newDepth + theirs(neighbour)`) found
    * this level (else -1).
    */
  private def expand(
      frontier: List[Long],
      ours: mutable.LongMap[Int],
      theirs: mutable.LongMap[Int],
      newDepth: Int,
      n: Int
  ): (List[Long], Int) = {
    val next = mutable.ListBuffer.empty[Long]
    var best = -1
    frontier.foreach { state =>
      val ns = neighbours(state, n)
      var i  = 0
      while (i < ns.length) {
        val nb = ns(i)
        if (!ours.contains(nb)) {
          theirs.get(nb) match {
            case Some(otherDepth) =>
              val total = newDepth + otherDepth
              if (best < 0 || total < best) best = total
            case None => ()
          }
          ours.update(nb, newDepth)
          next += nb
        }
        i += 1
      }
    }
    (next.toList, best)
  }
}
