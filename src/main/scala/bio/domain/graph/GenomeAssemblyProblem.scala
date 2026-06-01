package bio.domain.graph

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind LONG ("Genome Assembly as Shortest
  * Superstring") problem — see [[bio.algorithms.graph.GenomeAssembly.assemble]].
  *
  * Wraps the collection of DNA reads to be assembled into one contiguous
  * superstring. The smart constructor enforces:
  *   1. `reads.nonEmpty`, else `EmptyReadCollection`;
  *   2. `reads.size <= 50` (Rosalind cap), else `TooManyReads`;
  *   3. each read `length <= 1000` bp (per-read cap), else `ReadTooLong`.
  *
  * Failures are reported in the order empty → too-many → read-too-long (first
  * failure wins).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[GenomeAssemblyProblem.from]].
  */
sealed abstract case class GenomeAssemblyProblem(reads: Vector[DnaString])

object GenomeAssemblyProblem {
  private val MaxReads: Int  = 50
  private val MaxLength: Int = 1000

  def from(
      reads: Vector[DnaString]
  ): Either[GenomeAssemblyProblemError, GenomeAssemblyProblem] =
    if (reads.isEmpty)
      Left(GenomeAssemblyProblemError.EmptyReadCollection)
    else if (reads.size > MaxReads)
      Left(GenomeAssemblyProblemError.TooManyReads(reads.size, MaxReads))
    else
      firstOversized(reads) match {
        case Some(err) => Left(err)
        case None      => Right(new GenomeAssemblyProblem(reads) {})
      }

  private def firstOversized(
      reads: Vector[DnaString]
  ): Option[GenomeAssemblyProblemError] =
    reads.iterator.zipWithIndex.collectFirst {
      case (r, i) if r.value.length > MaxLength =>
        GenomeAssemblyProblemError.ReadTooLong(i, r.value.length, MaxLength)
    }
}
