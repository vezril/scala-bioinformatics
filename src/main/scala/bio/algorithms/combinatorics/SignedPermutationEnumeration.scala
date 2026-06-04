package bio.algorithms.combinatorics

import bio.domain.combinatorics.{PermutationLength, SignedPermutationProblem, SignedPermutations}

/** Enumerates every signed permutation of length `n` (Rosalind SIGN —
  * "Enumerating Oriented Gene Orderings").
  *
  * A signed permutation is one of the `n!` orderings of `{1, …, n}` with each
  * element independently assigned a `+` or `−` sign. The result is the Cartesian
  * product of the base permutations (reusing [[Permutations.enumerate]]) with the
  * `2^n` sign assignments — `n! * 2^n` permutations in total.
  *
  * Pure functional generation over immutable `Vector`s; the public [[enumerate]]
  * signature is total.
  */
object SignedPermutationEnumeration {

  def enumerate(problem: SignedPermutationProblem): SignedPermutations = {
    val n     = problem.n
    val signs = signCombinations(n)
    val signed = basePermutations(n).flatMap { perm =>
      signs.map(combo => perm.lazyZip(combo).map(_ * _))
    }
    SignedPermutations(signed)
  }

  /** The `n!` permutations of `{1, …, n}`, via the shared permutation generator.
    * `n` is pre-validated to `1 … 6`, so building the [[PermutationLength]] always
    * succeeds (the fallback is unreachable).
    */
  private def basePermutations(n: Int): Vector[Vector[Int]] =
    PermutationLength.from(n).map(Permutations.enumerate).getOrElse(Vector.empty)

  /** The `2^n` sign assignments: each a vector of `±1` (bit set → `−1`). */
  private def signCombinations(n: Int): Vector[Vector[Int]] =
    (0 until (1 << n)).toVector.map { mask =>
      (0 until n).toVector.map(i => if ((mask & (1 << i)) != 0) -1 else 1)
    }
}
