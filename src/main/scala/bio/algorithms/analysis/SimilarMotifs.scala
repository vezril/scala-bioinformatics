package bio.algorithms.analysis

import bio.domain.analysis.{SimilarMotif, SimilarMotifsProblem}

import scala.collection.mutable.ListBuffer

/** Finds, for a motif `s` and a genome `t`, **every** substring `t′` of `t`
  * whose unit-cost edit distance `d_E(s, t′)` is at most `k` (Rosalind KSIM
  * — "Finding All Similar Motifs"). Each hit is reported as a
  * [[bio.domain.analysis.SimilarMotif]] with its 1-based `location` in `t`
  * and its `length`.
  *
  * Edit distance is standard Levenshtein: insertion, deletion, and
  * substitution each cost `1`.
  *
  * **Algorithm — forward prune + per-end backward enumeration.**
  *
  * Let `m = |s|`, `n = |t|`.
  *
  *   1. Forward approximate-matching DP with a *free start* in `t`:
  *      `D(a)(b)` = the minimum edit distance between the motif prefix
  *      `s[0..a)` and any substring of `t` ending exactly at genome column
  *      `b`. Boundaries `D(0)(b) = 0` (empty prefix matches the empty
  *      substring anywhere) and `D(a)(0) = a`; recurrence
  *      `D(a)(b) = min(D(a-1)(b-1) + cost, D(a-1)(b) + 1, D(a)(b-1) + 1)`.
  *      The row `D(m)(·)` is computed with two rolling arrays; the **valid
  *      end columns** are `{ b : D(m)(b) <= k }`. Every qualifying
  *      substring ends at such a column (its end column's minimum can only
  *      be `<=` its own distance), so this is a complete, sound filter.
  *
  *   2. Per valid end column `b`, a backward DP recovers the *exact* lengths.
  *      A qualifying substring has `|len - m| <= k`, so its length lies in
  *      `[max(1, m - k), min(b, m + k)]`. `B(a)(c)` = the edit distance
  *      between the last `a` motif characters and the last `c` genome
  *      characters before `b` (boundaries `B(0)(c) = c`, `B(a)(0) = a`).
  *      `B(m)(c)` is then the exact distance of `t[b-c..b)`; whenever it is
  *      `<= k` the hit `SimilarMotif(b - c + 1, c)` is emitted.
  *
  * Distinct end columns yield distinct hits (an end column equals
  * `location + length - 1`), so no duplicates arise. The collected hits are
  * sorted by `(location, length)` before being returned.
  *
  * **Complexity.** Forward pass `O(m · n)` time, `O(n)` memory (rolling
  * rows). Each valid end's backward pass is `O(m · (m + k))` over a window
  * of `<= 2k + 1` candidate lengths.
  */
object SimilarMotifs {

  def findAll(problem: SimilarMotifsProblem): List[SimilarMotif] = {
    val s = problem.motif.value
    val t = problem.genome.value
    val k = problem.k
    val m = s.length
    val n = t.length

    // No non-empty substring exists in an empty genome.
    if (n == 0) return Nil

    // --- Pass 1: forward DP, free start in t, find valid end columns. ---
    // prev holds D(a-1)(·); after the loop it holds D(m)(·).
    var prev = Array.fill(n + 1)(0) // D(0)(b) = 0 for all b
    var a    = 1
    while (a <= m) {
      val curr = new Array[Int](n + 1)
      curr(0) = a // D(a)(0) = a
      val sa = s.charAt(a - 1)
      var b  = 1
      while (b <= n) {
        val cost = if (sa == t.charAt(b - 1)) 0 else 1
        curr(b) = math.min(
          prev(b - 1) + cost,
          math.min(prev(b) + 1, curr(b - 1) + 1)
        )
        b += 1
      }
      prev = curr
      a += 1
    }
    val forwardLast = prev // D(m)(·)

    // --- Pass 2: per valid end, backward DP to enumerate exact lengths. ---
    val hits = ListBuffer.empty[SimilarMotif]
    val lMin = math.max(1, m - k)

    var end = 1
    while (end <= n) {
      if (forwardLast(end) <= k) {
        val lMax = math.min(end, m + k)
        if (lMax >= lMin) {
          // backRow indexed by c = number of genome chars before `end`.
          // Initialise B(0)(c) = c.
          var back = Array.tabulate(lMax + 1)(identity)
          var aa   = 1
          while (aa <= m) {
            val cur  = new Array[Int](lMax + 1)
            cur(0) = aa // B(aa)(0) = aa
            val sChar = s.charAt(m - aa) // aa-th char from the right of s
            var c     = 1
            while (c <= lMax) {
              val cost =
                if (sChar == t.charAt(end - c)) 0 else 1 // c-th char from the right before `end`
              cur(c) = math.min(
                back(c - 1) + cost,
                math.min(back(c) + 1, cur(c - 1) + 1)
              )
              c += 1
            }
            back = cur
            aa += 1
          }
          // back now holds B(m)(·); emit hits within the length window.
          var c = lMin
          while (c <= lMax) {
            if (back(c) <= k) hits += SimilarMotif(end - c + 1, c)
            c += 1
          }
        }
      }
      end += 1
    }

    hits.toList.sortBy(h => (h.location, h.length))
  }
}
