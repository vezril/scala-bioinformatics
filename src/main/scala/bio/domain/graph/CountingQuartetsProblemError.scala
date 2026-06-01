package bio.domain.graph

/** Construction failures for [[CountingQuartetsProblem]] (Rosalind CNTQ). */
sealed trait CountingQuartetsProblemError
object CountingQuartetsProblemError {

  /** The leaf count was below the minimum `4`. */
  final case class BelowMinimum(value: Int, min: Int) extends CountingQuartetsProblemError

  /** The leaf count exceeded the maximum `5000`. */
  final case class ExceedsMaximum(value: Int, max: Int) extends CountingQuartetsProblemError

  /** The parsed tree's leaf count differed from the declared `n`. */
  final case class LeafCountMismatch(declared: Int, actual: Int)
      extends CountingQuartetsProblemError
}
