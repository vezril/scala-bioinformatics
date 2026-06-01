package bio.domain.graph

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind DBRU ("Constructing a De Bruijn
  * Graph") problem — see [[bio.algorithms.graph.DeBruijnGraphConstruction.construct]].
  *
  * Wraps the collection of (k+1)-mer `DnaString`s from which the de Bruijn
  * graph is built. DNA-character validity is owned upstream by `DnaString`, so
  * the smart constructor validates only the *shape* of the collection:
  *   1. `kmers.nonEmpty`, else `EmptyKmerCollection`;
  *   2. `kmers.size <= 1000` (Rosalind cap), else `TooManyKmers`;
  *   3. each k-mer `length >= 2` (needs `k >= 1` to split), else `KmerTooShort`;
  *   4. each k-mer `length <= 50` (per-k-mer cap), else `KmerTooLong`;
  *   5. all k-mers share the first k-mer's length, else `InconsistentLength`.
  *
  * Failures are reported in the order empty → too-many → (per-k-mer, in index
  * order) too-short → too-long → inconsistent-length (first failure wins).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[DeBruijnGraphProblem.from]].
  */
sealed abstract case class DeBruijnGraphProblem(kmers: Vector[DnaString])

object DeBruijnGraphProblem {
  private val MaxKmers: Int = 1000
  private val MinLength: Int = 2
  private val MaxLength: Int = 50

  def from(
      kmers: Vector[DnaString]
  ): Either[DeBruijnGraphProblemError, DeBruijnGraphProblem] =
    if (kmers.isEmpty)
      Left(DeBruijnGraphProblemError.EmptyKmerCollection)
    else if (kmers.size > MaxKmers)
      Left(DeBruijnGraphProblemError.TooManyKmers(kmers.size, MaxKmers))
    else
      firstBadKmer(kmers) match {
        case Some(err) => Left(err)
        case None      => Right(new DeBruijnGraphProblem(kmers) {})
      }

  /** Scan k-mers in index order, returning the first that violates the
    * too-short → too-long → inconsistent-length checks (in that precedence).
    * Inconsistent length is measured against the first k-mer's length.
    */
  private def firstBadKmer(
      kmers: Vector[DnaString]
  ): Option[DeBruijnGraphProblemError] = {
    val expected = kmers.head.value.length
    kmers.iterator.zipWithIndex.collectFirst {
      case (k, i) if k.value.length < MinLength =>
        DeBruijnGraphProblemError.KmerTooShort(i, k.value.length, MinLength)
      case (k, i) if k.value.length > MaxLength =>
        DeBruijnGraphProblemError.KmerTooLong(i, k.value.length, MaxLength)
      case (k, i) if k.value.length != expected =>
        DeBruijnGraphProblemError.InconsistentLength(i, k.value.length, expected)
    }
  }
}
