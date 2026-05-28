package bio.algorithms.analysis

import bio.domain.analysis.SplicedMotifProblem

/** Finds the leftmost-greedy 1-indexed positions of a target DNA pattern
  * inside a source DNA string as a *subsequence* — symbols in order, not
  * necessarily contiguous (Rosalind spec 37 — SSEQ, "Finding a Spliced
  * Motif").
  *
  * **Subsequence vs substring.** Where [[MotifLocations]] (SUBS, spec 9) finds
  * *contiguous* occurrences of the pattern (every position must match
  * consecutively), `SplicedMotif` finds *subsequence* occurrences where the
  * pattern's symbols can be separated by arbitrary gaps. Biologically this
  * models how an exon-derived protein subsequence sits inside the wider
  * intron-bearing pre-mRNA.
  *
  * **Algorithm.** Single-pass two-pointer walk: `i` over `source.value` and
  * `j` over `target.value`. On `source(i) == target(j)`, record `i + 1`
  * (1-indexed, matching Rosalind convention) and advance both pointers;
  * otherwise advance only `i`. Terminates when either pointer exhausts its
  * string. If `j == target.length` at exit, `target` is a subsequence of
  * `source` and we return `Some(indices)`; else `None`.
  *
  * **Leftmost-greedy convention.** When multiple valid index collections
  * exist (as is typical), this implementation returns the *lexicographically-
  * leftmost* one — i.e., each character is matched at the earliest possible
  * position. For the canonical Rosalind sample (`source = ACGTACGTGACG`,
  * `target = GTA`) the greedy answer is `Vector(3, 4, 5)`; the Rosalind
  * published answer `Vector(3, 8, 10)` is also a valid subsequence match (the
  * spec explicitly notes "you may return any one"). Tests assert the greedy
  * answer for determinism.
  *
  * **Empty target.** The empty string is vacuously a subsequence of every
  * string. The two-pointer walk's exit condition `j == target.length` is
  * satisfied immediately when `target` is empty, so the result is
  * `Some(Vector.empty)`.
  *
  * **Complexity:** `O(|source| + |target|)` time, `O(|target|)` space for the
  * result vector. At the Rosalind cap of `|s| ≤ 1000` the work is microseconds.
  */
object SplicedMotif {

  def find(problem: SplicedMotifProblem): Option[Vector[Int]] = {
    val s = problem.source.value
    val t = problem.target.value
    val n = s.length
    val m = t.length

    val builder = Vector.newBuilder[Int]
    var i       = 0
    var j       = 0
    while (i < n && j < m) {
      if (s.charAt(i) == t.charAt(j)) {
        builder += (i + 1)
        j += 1
      }
      i += 1
    }

    if (j == m) Some(builder.result()) else None
  }
}
