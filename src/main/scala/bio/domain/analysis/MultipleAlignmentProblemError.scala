package bio.domain.analysis

/** Construction failures for [[MultipleAlignmentProblem]]. */
sealed trait MultipleAlignmentProblemError
object MultipleAlignmentProblemError {

  /** The supplied collection of DNA strings did not contain exactly the
    * expected count (Rosalind MULT requires `expected == 4`).
    */
  final case class WrongNumberOfStrings(actual: Int, expected: Int)
      extends MultipleAlignmentProblemError

  /** A DNA string at position `index` exceeded the Rosalind MULT per-string
    * cap of `max` characters.
    */
  final case class StringTooLong(index: Int, length: Int, max: Int)
      extends MultipleAlignmentProblemError
}
