package bio.domain.graph

/** Construction failures for [[GenomeAssemblyProblem]]. */
sealed trait GenomeAssemblyProblemError
object GenomeAssemblyProblemError {

  /** The input collection was empty — at least one read is required. */
  case object EmptyReadCollection extends GenomeAssemblyProblemError

  /** The input collection exceeded the Rosalind LONG cap of 50 reads. */
  final case class TooManyReads(count: Int, max: Int) extends GenomeAssemblyProblemError

  /** A read exceeded the Rosalind LONG per-read cap of 1000 bp. `index` identifies
    * the first offending read.
    */
  final case class ReadTooLong(index: Int, length: Int, max: Int)
      extends GenomeAssemblyProblemError
}
