package bio.domain.graph

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind PCOV ("Genome Assembly with Perfect
  * Coverage") problem — see
  * [[bio.algorithms.graph.PerfectCoverageAssembly.assemble]].
  *
  * Wraps the collection of equal-length read `DnaString`s (the (k+1)-mers of an
  * unknown circular chromosome). DNA-character validity is owned upstream by
  * `DnaString`, so the smart constructor validates only the *shape* of the
  * collection:
  *   1. `kmers.nonEmpty`, else `EmptyKmerCollection`;
  *   2. each read `length >= 2` (needs `L >= 2` to split), else `KmerTooShort`;
  *   3. each read `length <= 50` (per-read cap), else `KmerTooLong`;
  *   4. all reads share the first read's length, else `InconsistentLength`.
  *
  * Failures are reported in the order empty → (per-k-mer, in index order)
  * too-short → too-long → inconsistent-length (first failure wins). Rosalind
  * states no read-count cap for PCOV, so none is imposed.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[PerfectCoverageProblem.from]].
  */
sealed abstract case class PerfectCoverageProblem(kmers: Vector[DnaString])

object PerfectCoverageProblem {
  private val MinLength: Int = 2
  private val MaxLength: Int = 50

  def from(
      kmers: Vector[DnaString]
  ): Either[PerfectCoverageProblemError, PerfectCoverageProblem] =
    if (kmers.isEmpty)
      Left(PerfectCoverageProblemError.EmptyKmerCollection)
    else
      firstBadKmer(kmers) match {
        case Some(err) => Left(err)
        case None      => Right(new PerfectCoverageProblem(kmers) {})
      }

  /** Scan reads in index order, returning the first that violates the
    * too-short → too-long → inconsistent-length checks (in that precedence).
    * Inconsistent length is measured against the first read's length.
    */
  private def firstBadKmer(
      kmers: Vector[DnaString]
  ): Option[PerfectCoverageProblemError] = {
    val expected = kmers.head.value.length
    kmers.iterator.zipWithIndex.collectFirst {
      case (k, i) if k.value.length < MinLength =>
        PerfectCoverageProblemError.KmerTooShort(i, k.value.length, MinLength)
      case (k, i) if k.value.length > MaxLength =>
        PerfectCoverageProblemError.KmerTooLong(i, k.value.length, MaxLength)
      case (k, i) if k.value.length != expected =>
        PerfectCoverageProblemError.InconsistentLength(i, k.value.length, expected)
    }
  }
}
