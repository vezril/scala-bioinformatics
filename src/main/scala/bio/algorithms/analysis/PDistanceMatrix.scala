package bio.algorithms.analysis

import bio.domain.analysis.{DistanceMatrix, DistanceMatrixProblem}
import bio.domain.nucleic.DnaString

/** Computes the Rosalind PDST ("Creating a Distance Matrix") p-distance matrix.
  *
  * For equal-length DNA strings `s_0 … s_{n-1}`, produces the `n × n` matrix `D`
  * with `D[i][j] = d_p(s_i, s_j)` — the *p-distance*, i.e. the number of
  * positions at which `s_i` and `s_j` differ (their Hamming distance, reusing
  * [[HammingDistance]]) divided by the common length `L`. The diagonal is `0`,
  * the matrix is symmetric, and an all-empty input (`L = 0`) yields a zero
  * matrix (no positions to differ).
  *
  * The fill is `O(n² · L)` and purely functional (`Vector.tabulate`); the public
  * [[compute]] signature is total. The equal-length invariant is guaranteed by
  * [[DistanceMatrixProblem]], so `HammingDistance` never returns `Left` here.
  */
object PDistanceMatrix {

  def compute(problem: DistanceMatrixProblem): DistanceMatrix = {
    val strings = problem.strings
    val n       = strings.length
    DistanceMatrix(Vector.tabulate(n, n)((i, j) => pDistance(strings(i), strings(j))))
  }

  /** The p-distance between two equal-length DNA strings (`0.0` when empty). */
  private def pDistance(a: DnaString, b: DnaString): Double = {
    val length = a.value.length
    if (length == 0) 0.0
    else HammingDistance.between(a, b).getOrElse(0).toDouble / length
  }
}
