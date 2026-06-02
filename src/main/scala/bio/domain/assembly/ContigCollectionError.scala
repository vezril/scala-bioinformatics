package bio.domain.assembly

/** Construction failures for [[ContigCollection]].
  *
  * DNA-character validity is handled upstream by `DnaString` and is not
  * re-encoded here; these cases describe only the ways the *shape* of a contig
  * collection can be invalid for the ASMQ ("Assessing Assembly Quality")
  * problem.
  */
sealed trait ContigCollectionError
object ContigCollectionError {

  /** The input collection was empty — at least one contig is required. */
  case object EmptyContigCollection extends ContigCollectionError

  /** The collection exceeded the Rosalind cap of 1000 contigs. */
  final case class TooManyContigs(count: Int, max: Int) extends ContigCollectionError

  /** A contig had length 0; the N-statistic is defined over positive lengths.
    * `index` identifies the first offending contig.
    */
  final case class EmptyContig(index: Int) extends ContigCollectionError

  /** The combined length of all contigs exceeded the Rosalind cap of 50000 bp. */
  final case class ExceedsTotalLength(total: Int, max: Int) extends ContigCollectionError
}
