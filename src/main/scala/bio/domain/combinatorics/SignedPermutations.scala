package bio.domain.combinatorics

/** Result of the Rosalind SIGN ("Enumerating Oriented Gene Orderings") problem:
  * every signed permutation of length `n` (see
  * [[bio.algorithms.combinatorics.SignedPermutationEnumeration.enumerate]]).
  *
  * `count` is the number of permutations (`n! * 2^n`); `format` renders the count
  * on the first line followed by each permutation space-separated on its own line.
  */
final case class SignedPermutations(permutations: Vector[Vector[Int]]) {
  def count: Int = permutations.size

  def format: String =
    (count.toString +: permutations.map(_.mkString(" "))).mkString("\n")
}
