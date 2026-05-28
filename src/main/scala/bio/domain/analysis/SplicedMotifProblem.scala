package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input bundle for the Rosalind SSEQ ("Finding a Spliced Motif")
  * problem — see [[bio.algorithms.analysis.SplicedMotif.find]].
  *
  * Bundles a source DNA string `source` (the haystack) with a target DNA
  * string `target` (the subsequence to locate). The smart constructor enforces
  * both Rosalind length caps:
  *   1. `source.value.length <= 1000`, else `SourceTooLong`;
  *   2. `target.value.length <= 1000`, else `TargetTooLong`.
  *
  * Empty `source` and/or empty `target` are accepted. An empty `target` is
  * vacuously a subsequence of any `source`, so the algorithm returns
  * `Some(Vector.empty)` in that case.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[SplicedMotifProblem.from]].
  */
sealed abstract case class SplicedMotifProblem(
    source: DnaString,
    target: DnaString
)

object SplicedMotifProblem {
  private val MaxLength: Int = 1000

  def from(
      source: DnaString,
      target: DnaString
  ): Either[SplicedMotifProblemError, SplicedMotifProblem] = {
    val srcLen = source.value.length
    val tgtLen = target.value.length
    if (srcLen > MaxLength)
      Left(SplicedMotifProblemError.SourceTooLong(srcLen, MaxLength))
    else if (tgtLen > MaxLength)
      Left(SplicedMotifProblemError.TargetTooLong(tgtLen, MaxLength))
    else
      Right(new SplicedMotifProblem(source, target) {})
  }
}
