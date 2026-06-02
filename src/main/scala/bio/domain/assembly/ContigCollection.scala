package bio.domain.assembly

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind ASMQ ("Assessing Assembly Quality")
  * problem — see [[bio.algorithms.assembly.AssemblyStatistics]].
  *
  * Wraps a collection of variable-length DNA contigs. DNA-character validity is
  * owned upstream by `DnaString`, so the smart constructor validates only the
  * *shape* of the collection:
  *   1. `contigs.nonEmpty`, else `EmptyContigCollection`;
  *   2. at most 1000 contigs, else `TooManyContigs`;
  *   3. each contig has positive length, else `EmptyContig`;
  *   4. combined length at most 50000 bp, else `ExceedsTotalLength`.
  *
  * Failures are reported in the order empty → too-many → (per-contig, in index
  * order) empty-contig → exceeds-total-length (first failure wins). Contigs are
  * intentionally variable length — that variability is what the N-statistic
  * summarizes — so there is no per-contig length cap or inconsistent-length check.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[ContigCollection.from]].
  */
sealed abstract case class ContigCollection(contigs: Vector[DnaString])

object ContigCollection {
  private val MaxContigs: Int     = 1000
  private val MaxTotalLength: Int = 50000

  def from(
      contigs: Vector[DnaString]
  ): Either[ContigCollectionError, ContigCollection] =
    if (contigs.isEmpty)
      Left(ContigCollectionError.EmptyContigCollection)
    else if (contigs.length > MaxContigs)
      Left(ContigCollectionError.TooManyContigs(contigs.length, MaxContigs))
    else
      firstEmptyContig(contigs) match {
        case Some(err) => Left(err)
        case None =>
          val total = contigs.iterator.map(_.value.length).sum
          if (total > MaxTotalLength)
            Left(ContigCollectionError.ExceedsTotalLength(total, MaxTotalLength))
          else
            Right(new ContigCollection(contigs) {})
      }

  /** The first contig (in index order) with length 0, if any. */
  private def firstEmptyContig(
      contigs: Vector[DnaString]
  ): Option[ContigCollectionError] =
    contigs.iterator.zipWithIndex.collectFirst {
      case (c, i) if c.value.isEmpty => ContigCollectionError.EmptyContig(i)
    }
}
