package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind KMP ("Speeding Up Motif Finding")
  * problem — see [[bio.algorithms.analysis.FailureArray.compute]].
  *
  * Wraps a non-empty [[DnaString]] (alphabet validation is delegated upstream to
  * [[DnaString.from]]; this wrapper only adds the non-empty check). Constructable
  * only via [[FailureArrayProblem.from]].
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor.
  */
sealed abstract case class FailureArrayProblem(dna: DnaString)

object FailureArrayProblem {

  def from(dna: DnaString): Either[FailureArrayProblemError, FailureArrayProblem] =
    if (dna.value.isEmpty) Left(FailureArrayProblemError.EmptySequence)
    else Right(new FailureArrayProblem(dna) {})
}
