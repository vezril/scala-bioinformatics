package bio.domain.nucleic

/** Construction failures for [[MotzkinMatchingProblem]]. */
sealed trait MotzkinMatchingProblemError
object MotzkinMatchingProblemError {

  /** The RNA string exceeded the Rosalind MOTZ cap of 300 nt. */
  final case class ExceedsMaxLength(length: Int, max: Int)
      extends MotzkinMatchingProblemError
}
