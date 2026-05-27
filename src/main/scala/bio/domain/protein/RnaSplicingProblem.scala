package bio.domain.protein

import bio.domain.nucleic.DnaString

/** Validated input bundle for the RNA-splicing algorithm
  * (Rosalind SPLC — see [[bio.algorithms.protein.RnaSplicing]]).
  *
  * Constructable only via [[RnaSplicingProblem.from]] which enforces a single
  * cross-input constraint: no intron may be empty. Each `DnaString` already validates
  * its own alphabet and length cap; the empty-intron check is the one degenerate case
  * that isn't captured by the per-element types.
  *
  * Validation behavior: returns `Left(RnaSplicingProblemError.EmptyIntron(index))`
  * carrying the 0-indexed position of the *first* empty intron in input order.
  *
  * Empty `source` and empty `introns` vector are both accepted — they have
  * well-defined trivial behavior in the algorithm (empty source → empty protein;
  * empty introns → direct transcription + translation of the source).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class RnaSplicingProblem(source: DnaString, introns: Vector[DnaString])

object RnaSplicingProblem {

  def from(
      source: DnaString,
      introns: Vector[DnaString]
  ): Either[RnaSplicingProblemError, RnaSplicingProblem] =
    introns.zipWithIndex.find { case (intron, _) => intron.value.isEmpty } match {
      case Some((_, index)) => Left(RnaSplicingProblemError.EmptyIntron(index))
      case None             => Right(new RnaSplicingProblem(source, introns) {})
    }
}
