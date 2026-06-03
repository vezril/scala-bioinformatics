package bio.domain.nucleic

/** Construction failures for [[WobbleMatchingProblem]]. */
sealed trait WobbleMatchingProblemError
object WobbleMatchingProblemError {

  /** The RNA string exceeded the Rosalind RNAS cap of 200 nt. */
  final case class SequenceTooLong(length: Int, max: Int)
      extends WobbleMatchingProblemError
}
