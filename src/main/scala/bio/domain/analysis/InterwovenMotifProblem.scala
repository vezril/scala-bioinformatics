package bio.domain.analysis

import bio.domain.nucleic.DnaString

/** Validated input for the Rosalind ITWV ("Finding Disjoint Motifs in a Gene")
  * problem — see [[bio.algorithms.analysis.InterwovenMotifs.compute]].
  *
  * Wraps the text `DnaString` and a `Vector` of pattern `DnaString`s. The smart
  * constructor enforces, with first-failure-wins ordering: at most 10 patterns,
  * text length ≤ 10000 bp, and each pattern length ≤ 10 bp (the Rosalind caps).
  * Character validity (`A`,`C`,`G`,`T`) is enforced upstream by [[DnaString]].
  * An empty pattern list is accepted (a degenerate `0 × 0` matrix).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[InterwovenMotifProblem.from]].
  */
sealed abstract case class InterwovenMotifProblem(
    text: DnaString,
    patterns: Vector[DnaString]
)

object InterwovenMotifProblem {
  private val MaxPatterns: Int      = 10
  private val MaxTextLength: Int    = 10000
  private val MaxPatternLength: Int = 10

  def from(
      text: DnaString,
      patterns: Vector[DnaString]
  ): Either[InterwovenMotifProblemError, InterwovenMotifProblem] =
    if (patterns.size > MaxPatterns)
      Left(InterwovenMotifProblemError.TooManyPatterns(patterns.size, MaxPatterns))
    else if (text.value.length > MaxTextLength)
      Left(InterwovenMotifProblemError.TextTooLong(text.value.length, MaxTextLength))
    else
      patterns.find(_.value.length > MaxPatternLength) match {
        case Some(p) =>
          Left(InterwovenMotifProblemError.PatternTooLong(p.value.length, MaxPatternLength))
        case None =>
          Right(new InterwovenMotifProblem(text, patterns) {})
      }
}
