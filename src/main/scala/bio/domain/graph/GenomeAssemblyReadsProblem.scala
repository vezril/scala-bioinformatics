package bio.domain.graph

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind GASM ("Genome Assembly Using Reads")
  * problem — see [[bio.algorithms.graph.GenomeAssemblyReads.assemble]].
  *
  * Wraps the collection of equal-length read `DnaString`s sampled (from either
  * strand) of an unknown circular chromosome. DNA-character validity is owned
  * upstream by `DnaString`, so the smart constructor validates only the *shape*
  * of the collection:
  *   1. `reads.nonEmpty`, else `EmptyReadCollection`;
  *   2. each read `length >= 2` (needs `L >= 2` to split), else `ReadTooShort`;
  *   3. each read `length <= 50` (per-read cap), else `ReadTooLong`;
  *   4. all reads share the first read's length, else `InconsistentLength`.
  *
  * Failures are reported in the order empty → (per-read, in index order)
  * too-short → too-long → inconsistent-length (first failure wins). Rosalind
  * states no read-count cap for GASM, so none is imposed.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[GenomeAssemblyReadsProblem.from]].
  */
sealed abstract case class GenomeAssemblyReadsProblem(reads: Vector[DnaString])

object GenomeAssemblyReadsProblem {
  private val MinLength: Int = 2
  private val MaxLength: Int = 50

  def from(
      reads: Vector[DnaString]
  ): Either[GenomeAssemblyReadsProblemError, GenomeAssemblyReadsProblem] =
    if (reads.isEmpty)
      Left(GenomeAssemblyReadsProblemError.EmptyReadCollection)
    else
      firstBadRead(reads) match {
        case Some(err) => Left(err)
        case None      => Right(new GenomeAssemblyReadsProblem(reads) {})
      }

  /** Scan reads in index order, returning the first that violates the
    * too-short → too-long → inconsistent-length checks (in that precedence).
    * Inconsistent length is measured against the first read's length.
    */
  private def firstBadRead(
      reads: Vector[DnaString]
  ): Option[GenomeAssemblyReadsProblemError] = {
    val expected = reads.head.value.length
    reads.iterator.zipWithIndex.collectFirst {
      case (r, i) if r.value.length < MinLength =>
        GenomeAssemblyReadsProblemError.ReadTooShort(i, r.value.length, MinLength)
      case (r, i) if r.value.length > MaxLength =>
        GenomeAssemblyReadsProblemError.ReadTooLong(i, r.value.length, MaxLength)
      case (r, i) if r.value.length != expected =>
        GenomeAssemblyReadsProblemError.InconsistentLength(i, r.value.length, expected)
    }
  }
}
