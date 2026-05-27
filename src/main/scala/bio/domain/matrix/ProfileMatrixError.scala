package bio.domain.matrix

import bio.domain.nucleic.DnaNucleotide

/** Construction failures for [[ProfileMatrix]]. */
sealed trait ProfileMatrixError
object ProfileMatrixError {

  /** The supplied counts map did not contain an entry for `missing`. A `ProfileMatrix`
    * is total over the 4-nucleotide alphabet, so all four keys are required.
    */
  final case class MissingNucleotide(missing: DnaNucleotide) extends ProfileMatrixError

  /** The four `Vector[Int]` count columns had different lengths. `lengths` reports the
    * observed length for each nucleotide so callers can spot the offending row(s).
    */
  final case class UnequalColumnLengths(lengths: Map[DnaNucleotide, Int])
      extends ProfileMatrixError
}
