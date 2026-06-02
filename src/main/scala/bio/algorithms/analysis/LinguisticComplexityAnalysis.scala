package bio.algorithms.analysis

import bio.domain.analysis.{LinguisticComplexity, LinguisticComplexityProblem}

import scala.annotation.tailrec

/** Computes the linguistic complexity `lc(s) = sub(s) / m(4,n)` of a DNA string —
  * Rosalind LING ("Linguistic Complexity of a Genome").
  *
  *   - `sub(s)` is the number of distinct non-empty substrings of `s`, obtained as
  *     `n(n+1)/2 - Σ LCP[i]` from the suffix array and its LCP array (the suffix-array
  *     dual of "total suffix-tree edge length").
  *   - `m(4,n) = Σ_{k=1}^{n} min(4^k, n-k+1)` is the maximum possible number of distinct
  *     substrings over the 4-letter DNA alphabet.
  *
  * The suffix array is built by prefix doubling and the LCP sum by Kasai's algorithm —
  * O(n log n)-ish, scaling to the 100 kbp Rosalind bound. Pure and total: immutable
  * `Vector`s, `foldLeft`, and tail recursion only — no `var`, `while`, or mutable
  * collection. The empty string yields `0.0` (its `m` is `0`).
  */
object LinguisticComplexityAnalysis {

  private val Alphabet: Int = 4

  def compute(problem: LinguisticComplexityProblem): LinguisticComplexity = {
    val s = problem.dna.value
    val n = s.length
    if (n == 0) LinguisticComplexity(0.0)
    else {
      val sub = n.toLong * (n + 1) / 2 - sumLcp(s)
      val m   = maxSubstrings(Alphabet, n)
      LinguisticComplexity(sub.toDouble / m.toDouble)
    }
  }

  /** Suffix array of `s` by prefix doubling (O(n log² n)). */
  private def suffixArray(s: String): Vector[Int] = {
    val n       = s.length
    val indices = (0 until n).toVector

    @tailrec
    def doubling(k: Int, rank: Vector[Int]): Vector[Int] = {
      def key(i: Int): (Int, Int) = (rank(i), if (i + k < n) rank(i + k) else -1)
      val sorted = indices.sortBy(key)
      // Recompute ranks from the sorted order.
      val newRank = sorted.iterator
        .foldLeft((Vector.fill(n)(0), 0, key(sorted.head))) {
          case ((ranks, r, prevKey), i) =>
            val rr = if (key(i) == prevKey) r else r + 1
            (ranks.updated(i, rr), rr, key(i))
        }
        ._1
      if (newRank(sorted.last) == n - 1 || k >= n) sorted
      else doubling(k * 2, newRank)
    }

    if (n == 0) Vector.empty
    else doubling(1, indices.map(i => s(i).toInt))
  }

  /** Sum of the LCP array of `s` via Kasai's algorithm (O(n)). */
  private def sumLcp(s: String): Long = {
    val n  = s.length
    val sa = suffixArray(s)
    // rank(i) = position of suffix i in the suffix array.
    val rank = sa.zipWithIndex.sortBy(_._1).map(_._2)

    (0 until n)
      .foldLeft((0, 0L)) { case ((h, sum), i) =>
        val r = rank(i)
        if (r == 0) (0, sum)
        else {
          val j  = sa(r - 1)
          val hh = matchLength(s, i, j, h, n)
          (math.max(hh - 1, 0), sum + hh)
        }
      }
      ._2
  }

  /** Length of the common prefix of suffixes `i` and `j`, starting the comparison at
    * offset `from` (Kasai's carried match length).
    */
  private def matchLength(s: String, i: Int, j: Int, from: Int, n: Int): Int = {
    @tailrec
    def go(m: Int): Int =
      if (i + m < n && j + m < n && s(i + m) == s(j + m)) go(m + 1) else m
    go(from)
  }

  /** `m(a,n) = Σ_{k=1}^{n} min(a^k, n-k+1)`, with an overflow-safe capped power. */
  private def maxSubstrings(a: Int, n: Int): Long =
    (1 to n).foldLeft(0L) { (acc, k) =>
      val positions = (n - k + 1).toLong
      acc + math.min(powCapped(a, k, positions), positions)
    }

  /** `a^k`, but stops multiplying once it exceeds `cap` (avoids `Long` overflow). */
  private def powCapped(a: Int, k: Int, cap: Long): Long = {
    @tailrec
    def go(i: Int, acc: Long): Long =
      if (i >= k || acc > cap) acc else go(i + 1, acc * a)
    go(0, 1L)
  }
}
