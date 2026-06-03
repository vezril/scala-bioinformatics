package bio.domain.nucleic

/** Construction failures for [[MaximumMatchingProblem]] (Rosalind MMCH). */
sealed trait MaximumMatchingProblemError
object MaximumMatchingProblemError {

  /** The RNA string exceeded the Rosalind MMCH cap of 100 nt. */
  final case class ExceedsMaxLength(length: Int, max: Int) extends MaximumMatchingProblemError
}
