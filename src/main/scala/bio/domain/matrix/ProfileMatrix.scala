package bio.domain.matrix

import bio.domain.nucleic.DnaNucleotide

/** A 4-row per-column-count matrix indexed by [[DnaNucleotide]].
  *
  * Conceptually the Rosalind "profile" matrix:
  * {{{
  *   A: 5 1 0 0 5 5 0 0
  *   C: 0 0 1 4 2 0 6 1
  *   G: 1 1 6 3 0 1 0 0
  *   T: 1 5 0 0 0 1 1 6
  * }}}
  *
  * Stored as `Map[DnaNucleotide, Vector[Int]]` to keep lookups type-safe and to let
  * algorithms iterate over the nucleotide ADT uniformly. Convenience accessors `.a`,
  * `.c`, `.g`, `.t`, `.width` give the row-by-row view the Rosalind output format
  * wants.
  *
  * Invariants enforced by [[ProfileMatrix.from]]:
  *   - the map contains all four `DnaNucleotide` keys
  *   - all four `Vector[Int]` values have the same length (the matrix width)
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor. Use [[ProfileMatrix.unsafeFrom]] only
  * from trusted callers that have already established both invariants.
  */
sealed abstract case class ProfileMatrix(counts: Map[DnaNucleotide, Vector[Int]]) {
  def a: Vector[Int] = counts(DnaNucleotide.A)
  def c: Vector[Int] = counts(DnaNucleotide.C)
  def g: Vector[Int] = counts(DnaNucleotide.G)
  def t: Vector[Int] = counts(DnaNucleotide.T)
  def width: Int     = a.length
}

object ProfileMatrix {
  private val RequiredKeys: Vector[DnaNucleotide] =
    Vector(DnaNucleotide.A, DnaNucleotide.C, DnaNucleotide.G, DnaNucleotide.T)

  def from(
      counts: Map[DnaNucleotide, Vector[Int]]
  ): Either[ProfileMatrixError, ProfileMatrix] =
    RequiredKeys.find(!counts.contains(_)) match {
      case Some(missing) => Left(ProfileMatrixError.MissingNucleotide(missing))
      case None =>
        val lengths = RequiredKeys.map(n => n -> counts(n).length).toMap
        if (lengths.values.toSet.size > 1)
          Left(ProfileMatrixError.UnequalColumnLengths(lengths))
        else
          Right(new ProfileMatrix(counts) {})
    }

  /** Internal constructor for trusted callers that have already established both
    * invariants (all four keys present, all four vectors of equal length). Bypasses
    * validation. Use [[from]] for any externally-sourced input.
    */
  private[bio] def unsafeFrom(counts: Map[DnaNucleotide, Vector[Int]]): ProfileMatrix =
    new ProfileMatrix(counts) {}
}
