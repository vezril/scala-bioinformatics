package bio.domain.graph

/** Construction failures for [[GenomeAssemblyReadsProblem]].
  *
  * DNA-character validity is handled upstream by `DnaString` and is not
  * re-encoded here; these cases describe only the ways the *shape* of a
  * genome-assembly read collection can be invalid.
  */
sealed trait GenomeAssemblyReadsProblemError
object GenomeAssemblyReadsProblemError {

  /** The input collection was empty — at least one read is required. */
  case object EmptyReadCollection extends GenomeAssemblyReadsProblemError

  /** A read was shorter than the minimum length needed to split into a
    * length-`(L-1)` prefix and suffix (min 2). `index` identifies the first
    * offending read.
    */
  final case class ReadTooShort(index: Int, length: Int, min: Int)
      extends GenomeAssemblyReadsProblemError

  /** A read exceeded the Rosalind per-read cap of 50 symbols. `index`
    * identifies the first offending read.
    */
  final case class ReadTooLong(index: Int, length: Int, max: Int)
      extends GenomeAssemblyReadsProblemError

  /** A read's length differed from the length shared by the preceding reads.
    * `index` identifies the first offending read; `expected` is the first
    * read's length.
    */
  final case class InconsistentLength(index: Int, length: Int, expected: Int)
      extends GenomeAssemblyReadsProblemError
}
