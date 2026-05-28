package bio.algorithms.analysis

import bio.domain.analysis.SharedMotifProblem

/** Finds a *longest common substring* of a collection of DNA strings —
  * specifically, the lexicographically-smallest substring of maximum length
  * that appears in every input (Rosalind spec 38 — LCSM, "Finding a Shared
  * Motif").
  *
  * **Vs related capabilities.**
  *   - [[MotifLocations]] (SUBS, spec 9) finds every occurrence of *one
  *     given* substring inside *one* source string.
  *   - [[SplicedMotif]] (SSEQ, spec 37) finds *one given* pattern as a
  *     *subsequence* (non-contiguous) inside one source.
  *   - `SharedMotif` (this) discovers the *longest* substring (contiguous)
  *     common to *every* string in a multi-string collection.
  *
  * **Algorithm.** Binary search on candidate length `L ∈ {1, ..., |shortest|}`:
  *   1. Build a `Set[String]` of length-`L` substrings of every input.
  *   2. Intersect all the sets. Empty ⇒ `L` infeasible; non-empty ⇒ feasible.
  *   3. After binary search settles, return the `.min` (lex-smallest) element
  *      of the intersection at the maximum feasible `L`.
  *
  * **Lex-smallest tie-break.** When multiple length-`L_max` substrings are
  * common (as is typical), the spec permits any. We pick the
  * lexicographically-smallest for deterministic tests — which reproduces the
  * canonical Rosalind sample answer `"AC"` (smaller than the equally-valid
  * `"CA"` and `"TA"`).
  *
  * **Empty-input short-circuit.** If any input string is empty, no non-empty
  * common substring exists; the algorithm returns `""` directly.
  *
  * **Complexity.** `O(k · n · log n)` time, `O(k · n)` memory. At the
  * Rosalind cap of `k = 100`, `n = 1000` the worst case is ~`10⁶` character
  * ops plus hashing — milliseconds.
  */
object SharedMotif {

  def find(problem: SharedMotifProblem): String = {
    val strings = problem.sequences.map(_.value)
    if (strings.exists(_.isEmpty)) return ""

    val shortest = strings.minBy(_.length)
    var best     = ""
    var lo       = 1
    var hi       = shortest.length

    while (lo <= hi) {
      val mid = (lo + hi) / 2
      commonOfLength(strings, shortest, mid) match {
        case Some(substr) =>
          best = substr
          lo = mid + 1
        case None =>
          hi = mid - 1
      }
    }

    best
  }

  /** Returns the lex-smallest length-`L` substring common to every string in
    * `strings`, or `None` if no such substring exists. Builds a `Set[String]`
    * per string, intersects them all, short-circuits if any intersection
    * becomes empty.
    */
  private def commonOfLength(
      strings: Vector[String],
      shortest: String,
      L: Int
  ): Option[String] = {
    val seed = substringsOfLength(shortest, L)
    val intersected = strings.foldLeft(Option(seed)) { (acc, s) =>
      acc.flatMap { candidates =>
        val next = candidates.intersect(substringsOfLength(s, L))
        if (next.isEmpty) None else Some(next)
      }
    }
    intersected.map(_.min)
  }

  private def substringsOfLength(s: String, L: Int): Set[String] = {
    val builder = Set.newBuilder[String]
    var i       = 0
    val end     = s.length - L
    while (i <= end) {
      builder += s.substring(i, i + L)
      i += 1
    }
    builder.result()
  }
}
