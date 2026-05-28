package bio.domain.analysis

/** Construction failures for [[SharedMotifProblem]]. */
sealed trait SharedMotifProblemError
object SharedMotifProblemError {

  /** The input collection was empty — at least one DNA string is required. */
  case object EmptyCollection extends SharedMotifProblemError

  /** The input collection exceeded the Rosalind LCSM cap of 100 strings. */
  final case class TooManyStrings(count: Int, max: Int)
      extends SharedMotifProblemError

  /** A DNA string in the collection exceeded the Rosalind LCSM per-string cap
    * of 1000 nt. `index` identifies the first offending row.
    */
  final case class StringTooLong(index: Int, length: Int, max: Int)
      extends SharedMotifProblemError
}
