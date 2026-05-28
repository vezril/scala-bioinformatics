package bio.domain.analysis

/** Construction failures for [[FailureArrayProblem]]. */
sealed trait FailureArrayProblemError
object FailureArrayProblemError {

  /** The wrapped DNA sequence was empty. The failure array of the empty string is
    * technically well-defined as the empty vector, but the Rosalind KMP problem
    * always has at least one character — we treat empty input as a programmer
    * error rather than silently emitting an empty result.
    */
  case object EmptySequence extends FailureArrayProblemError
}
