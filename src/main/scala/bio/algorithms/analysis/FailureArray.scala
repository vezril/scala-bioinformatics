package bio.algorithms.analysis

import bio.domain.analysis.FailureArrayProblem

/** Computes the Knuth-Morris-Pratt failure array of a DNA string
  * (Rosalind KMP — "Speeding Up Motif Finding").
  *
  * The failure array `P` of `s` (length `n`) is defined by:
  *
  *   `P(i) = length of the longest **proper** prefix of s[0..i] (inclusive)
  *          that is also a suffix of s[0..i]`
  *
  * with `P(0) = 0` by convention. The returned `Vector` is 0-indexed; Rosalind's
  * 1-indexed `P[k]` corresponds to `result(k - 1)`.
  *
  * **Algorithm:** the classic two-pointer KMP table build. Walk `i` from 1 to
  * `n - 1` maintaining `j` as the current candidate prefix length. On mismatch,
  * fall back via `j = P(j - 1)`; on match, increment `j`. Each character is
  * examined at most twice → `O(n)` time and `O(n)` space. The inner mutation
  * is contained to a private helper; the public surface returns an immutable
  * `Vector[Int]`.
  *
  * **Worked example.** For `s = "CAGCATGGTATCACAGCAGAG"` the algorithm produces
  * `Vector(0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 3, 4, 5, 3, 0, 0)`,
  * matching Rosalind's published sample answer.
  *
  * **Complexity:** `O(n)` time, `O(n)` space — uses a local `Array[Int]` for
  * scratch, materialised to `Vector` on return.
  */
object FailureArray {

  def compute(problem: FailureArrayProblem): Vector[Int] = {
    val s = problem.dna.value
    val n = s.length
    val p = new Array[Int](n)

    var i = 1
    var j = 0
    while (i < n) {
      while (j > 0 && s.charAt(i) != s.charAt(j)) j = p(j - 1)
      if (s.charAt(i) == s.charAt(j)) j += 1
      p(i) = j
      i += 1
    }

    p.toVector
  }
}
