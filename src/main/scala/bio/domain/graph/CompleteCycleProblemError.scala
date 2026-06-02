package bio.domain.graph

/** Construction failures for [[CompleteCycleProblem]] (Rosalind GREP — "Genome
  * Assembly with Perfect Coverage and Repeats").
  *
  * DNA-character validity is handled upstream by `DnaString` and is not re-encoded
  * here; these cases describe only the ways the *shape* of a (k+1)-mer read
  * collection can be invalid.
  */
sealed trait CompleteCycleProblemError
object CompleteCycleProblemError {

  /** The input collection was empty — at least one read is required. */
  case object EmptyKmerCollection extends CompleteCycleProblemError

  /** The collection held more reads than the GREP cap (the circular chromosome
    * has length ≤ 50, and a circular assembly's length equals its edge count).
    */
  final case class TooManyReads(count: Int, max: Int)
      extends CompleteCycleProblemError

  /** A read was shorter than the minimum length needed to split into a
    * length-`(L-1)` prefix and suffix (min 2). `index` identifies the first
    * offending read.
    */
  final case class KmerTooShort(index: Int, length: Int, min: Int)
      extends CompleteCycleProblemError

  /** A read exceeded the GREP (k+1)-mer cap of 6 symbols (k ≤ 5). `index`
    * identifies the first offending read.
    */
  final case class KmerTooLong(index: Int, length: Int, max: Int)
      extends CompleteCycleProblemError

  /** A read's length differed from the first read's length. `index` identifies
    * the first offending read; `expected` is the first read's length.
    */
  final case class InconsistentLength(index: Int, length: Int, expected: Int)
      extends CompleteCycleProblemError
}
