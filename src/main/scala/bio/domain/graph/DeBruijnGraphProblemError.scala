package bio.domain.graph

/** Construction failures for [[DeBruijnGraphProblem]].
  *
  * DNA-character validity is handled upstream by `DnaString` and is not
  * re-encoded here; these cases describe only the ways the *shape* of a
  * (k+1)-mer collection can be invalid.
  */
sealed trait DeBruijnGraphProblemError
object DeBruijnGraphProblemError {

  /** The input collection was empty — at least one k-mer is required. */
  case object EmptyKmerCollection extends DeBruijnGraphProblemError

  /** The input collection exceeded the Rosalind DBRU cap of 1000 k-mers. */
  final case class TooManyKmers(count: Int, max: Int) extends DeBruijnGraphProblemError

  /** A k-mer was shorter than the minimum length needed to split into a prefix
    * and suffix (min 2). `index` identifies the first offending k-mer.
    */
  final case class KmerTooShort(index: Int, length: Int, min: Int)
      extends DeBruijnGraphProblemError

  /** A k-mer exceeded the Rosalind per-k-mer cap of 50 symbols. `index`
    * identifies the first offending k-mer.
    */
  final case class KmerTooLong(index: Int, length: Int, max: Int)
      extends DeBruijnGraphProblemError

  /** A k-mer's length differed from the length shared by the preceding k-mers.
    * `index` identifies the first offending k-mer; `expected` is the first
    * k-mer's length.
    */
  final case class InconsistentLength(index: Int, length: Int, expected: Int)
      extends DeBruijnGraphProblemError
}
