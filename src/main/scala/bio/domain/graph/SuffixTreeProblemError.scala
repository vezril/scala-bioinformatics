package bio.domain.graph

/** Validation errors for [[SuffixTreeProblem]] (Rosalind SUFF). */
sealed trait SuffixTreeProblemError

object SuffixTreeProblemError {

  /** The input DNA string exceeds the maximum allowed length. */
  final case class SequenceTooLong(length: Int, max: Int) extends SuffixTreeProblemError
}
