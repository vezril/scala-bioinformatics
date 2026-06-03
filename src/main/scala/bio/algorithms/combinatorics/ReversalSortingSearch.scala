package bio.algorithms.combinatorics

import bio.domain.combinatorics.{Reversal, ReversalDistanceProblem, ReversalSorting}

import scala.collection.mutable

/** Solves the Rosalind SORT ("Sorting by Reversals") problem: the reversal
  * distance between two permutations together with an explicit sequence of
  * interval reversals that sorts the source into the target.
  *
  * **Algorithm.** As in [[ReversalDistanceSearch]], the target is normalised to
  * the identity (the source is relabelled through the target's inverse), which
  * is safe because a reversal is a *positional* operation — relabelling values
  * commutes with reversing positions, so the position-intervals that sort the
  * relabelled source into the identity are exactly those that sort the original
  * source into the target. Bidirectional BFS over the `C(n,2)` interval
  * reversals finds the meeting point; parent-state and parent-interval maps on
  * both frontiers let us reconstruct the path: the forward half (start → meet,
  * reversed) followed by the backward half (meet → goal) gives the reversal
  * sequence, of length `d_rev`.
  *
  * Permutations (`n ≤ 10`, values `1..10`) pack into a `Long` (4 bits/element);
  * intervals pack into an `Int` (`i * n + j`, 0-based). Imperative BFS internals
  * are confined here; the public [[sort]] signature is pure and total.
  */
object ReversalSortingSearch {

  def sort(problem: ReversalDistanceProblem): ReversalSorting = {
    val source = problem.source.values
    val target = problem.target.values
    val n      = source.length

    if (n <= 1) ReversalSorting(0, Vector.empty)
    else {
      val start = encode(relabelToIdentity(source.toArray, target.toArray))
      val goal  = encode(Array.tabulate(n)(i => i + 1))
      if (start == goal) ReversalSorting(0, Vector.empty)
      else bidirectional(start, goal, n)
    }
  }

  private def relabelToIdentity(source: Array[Int], target: Array[Int]): Array[Int] = {
    val rank = new Array[Int](target.length + 1) // value -> 1-based position in target
    var i    = 0
    while (i < target.length) {
      rank(target(i)) = i + 1
      i += 1
    }
    source.map(rank)
  }

  private def encode(perm: Array[Int]): Long = {
    var key = 0L
    var i   = 0
    while (i < perm.length) {
      key = (key << 4) | perm(i).toLong
      i += 1
    }
    key
  }

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

  /** Neighbour keys paired with the packed 0-based interval (`i * n + j`) used. */
  private def neighbours(key: Long, n: Int): Array[(Long, Int)] = {
    val perm = decode(key, n)
    val out  = new Array[(Long, Int)](n * (n - 1) / 2)
    var idx  = 0
    var i    = 0
    while (i < n) {
      var j = i + 1
      while (j < n) {
        reverse(perm, i, j)
        out(idx) = (encode(perm), i * n + j)
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

  /** A side of the bidirectional search: depth, parent-state, and via-interval maps. */
  private final class Side(root: Long) {
    val depth: mutable.LongMap[Int]   = mutable.LongMap[Int](root -> 0)
    val parent: mutable.LongMap[Long] = mutable.LongMap.empty[Long]
    val via: mutable.LongMap[Int]     = mutable.LongMap.empty[Int]
  }

  private def bidirectional(start: Long, goal: Long, n: Int): ReversalSorting = {
    val s = new Side(start)
    val g = new Side(goal)
    var frontierS = List(start)
    var frontierG = List(goal)

    while (true) {
      val expandStart = frontierS.size <= frontierG.size
      val (frontier, ours, theirs) =
        if (expandStart) (frontierS, s, g) else (frontierG, g, s)

      val next     = mutable.ListBuffer.empty[Long]
      var bestNode = 0L
      var bestTot  = Int.MaxValue
      frontier.foreach { state =>
        val ns = neighbours(state, n)
        var i  = 0
        while (i < ns.length) {
          val (nb, interval) = ns(i)
          if (!ours.depth.contains(nb)) {
            ours.depth.update(nb, ours.depth(state) + 1)
            ours.parent.update(nb, state)
            ours.via.update(nb, interval)
            theirs.depth.get(nb) match {
              case Some(od) =>
                val total = ours.depth(nb) + od
                if (total < bestTot) { bestTot = total; bestNode = nb }
              case None => ()
            }
            next += nb
          }
          i += 1
        }
      }

      if (bestTot != Int.MaxValue)
        return reconstruct(bestNode, s, g, bestTot, n)

      if (expandStart) frontierS = next.toList else frontierG = next.toList
    }
    // unreachable
    ReversalSorting(0, Vector.empty)
  }

  /** Stitch the forward half (start → meet, reversed) and backward half
    * (meet → goal) into the reversal sequence.
    */
  private def reconstruct(meet: Long, s: Side, g: Side, distance: Int, n: Int): ReversalSorting = {
    def toReversal(packed: Int): Reversal = Reversal(packed / n + 1, packed % n + 1)

    // Forward: walk meet -> start via s.parent, collect intervals, then reverse.
    val forward = mutable.ListBuffer.empty[Reversal]
    var x       = meet
    while (s.parent.contains(x)) {
      forward += toReversal(s.via(x))
      x = s.parent(x)
    }
    val forwardOrdered = forward.reverse.toVector

    // Backward: walk meet -> goal via g.parent, collect intervals in walk order.
    val backward = mutable.ListBuffer.empty[Reversal]
    var y        = meet
    while (g.parent.contains(y)) {
      backward += toReversal(g.via(y))
      y = g.parent(y)
    }

    ReversalSorting(distance, forwardOrdered ++ backward.toVector)
  }
}
