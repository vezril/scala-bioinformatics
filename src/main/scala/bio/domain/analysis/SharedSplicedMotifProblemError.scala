package bio.domain.analysis

/** Construction failures for [[SharedSplicedMotifProblem]]. */
sealed trait SharedSplicedMotifProblemError
object SharedSplicedMotifProblemError {

  /** The left-hand DNA string exceeded the Rosalind LCSQ cap of 1000 nt. */
  final case class LeftTooLong(length: Int, max: Int)
      extends SharedSplicedMotifProblemError

  /** The right-hand DNA string exceeded the Rosalind LCSQ cap of 1000 nt. */
  final case class RightTooLong(length: Int, max: Int)
      extends SharedSplicedMotifProblemError
}
