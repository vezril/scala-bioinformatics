package bio.domain.graph

import bio.domain.nucleic.DnaString

/** Validated input for the Rosalind SUFF ("Encoding Suffix Trees") problem — see
  * [[bio.algorithms.graph.SuffixTreeConstruction.encode]].
  *
  * Wraps the DNA string `s` as a [[DnaString]] of length `≤ 1000` bp. The terminator
  * `$` is appended internally by the algorithm to form `s$`; it is not part of the
  * stored `DnaString`. Character validity is owned upstream by `DnaString`, so the only
  * additional rule is the length cap (`SequenceTooLong`).
  *
  * The empty string is accepted (`s$ = "$"` yields a single leaf edge).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[SuffixTreeProblem.from]].
  */
sealed abstract case class SuffixTreeProblem(dna: DnaString)

object SuffixTreeProblem {
  private val MaxLength: Int = 1000

  def from(dna: DnaString): Either[SuffixTreeProblemError, SuffixTreeProblem] = {
    val length = dna.value.length
    if (length > MaxLength)
      Left(SuffixTreeProblemError.SequenceTooLong(length, MaxLength))
    else
      Right(new SuffixTreeProblem(dna) {})
  }
}
