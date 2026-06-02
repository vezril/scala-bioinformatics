package bio.domain.graph

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind GREP ("Genome Assembly with Perfect
  * Coverage and Repeats") problem — see
  * [[bio.algorithms.graph.CompleteCycleAssembly.assemble]].
  *
  * Wraps the collection of equal-length read `DnaString`s (the (k+1)-mers, k ≤ 5,
  * of an unknown circular chromosome). Unlike [[PerfectCoverageProblem]], **repeated
  * reads are preserved** — a (k+1)-mer occurring twice is two edges of the de Bruijn
  * graph, which is exactly what produces multiple complete cycles.
  *
  * DNA-character validity is owned upstream by `DnaString`, so the smart constructor
  * validates only the *shape* of the collection:
  *   1. `kmers.nonEmpty`, else `EmptyKmerCollection`;
  *   2. at most 50 reads, else `TooManyReads` (the circular chromosome length ≤ 50,
  *      and a circular assembly's length equals its edge count);
  *   3. each read `length >= 2` (needs `L >= 2` to split), else `KmerTooShort`;
  *   4. each read `length <= 6` (k ≤ 5 ⇒ k+1 ≤ 6), else `KmerTooLong`;
  *   5. all reads share the first read's length, else `InconsistentLength`.
  *
  * Failures are reported in the order empty → too-many → (per-read, in index order)
  * too-short → too-long → inconsistent-length (first failure wins).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via
  * [[CompleteCycleProblem.from]].
  */
sealed abstract case class CompleteCycleProblem(kmers: Vector[DnaString])

object CompleteCycleProblem {
  private val MinLength: Int = 2
  private val MaxLength: Int = 6
  private val MaxReads: Int  = 50

  def from(
      kmers: Vector[DnaString]
  ): Either[CompleteCycleProblemError, CompleteCycleProblem] =
    if (kmers.isEmpty)
      Left(CompleteCycleProblemError.EmptyKmerCollection)
    else if (kmers.size > MaxReads)
      Left(CompleteCycleProblemError.TooManyReads(kmers.size, MaxReads))
    else
      firstBadKmer(kmers) match {
        case Some(err) => Left(err)
        case None      => Right(new CompleteCycleProblem(kmers) {})
      }

  /** Scan reads in index order, returning the first that violates the
    * too-short → too-long → inconsistent-length checks (in that precedence).
    * Inconsistent length is measured against the first read's length.
    */
  private def firstBadKmer(
      kmers: Vector[DnaString]
  ): Option[CompleteCycleProblemError] = {
    val expected = kmers.head.value.length
    kmers.iterator.zipWithIndex.collectFirst {
      case (k, i) if k.value.length < MinLength =>
        CompleteCycleProblemError.KmerTooShort(i, k.value.length, MinLength)
      case (k, i) if k.value.length > MaxLength =>
        CompleteCycleProblemError.KmerTooLong(i, k.value.length, MaxLength)
      case (k, i) if k.value.length != expected =>
        CompleteCycleProblemError.InconsistentLength(i, k.value.length, expected)
    }
  }
}
