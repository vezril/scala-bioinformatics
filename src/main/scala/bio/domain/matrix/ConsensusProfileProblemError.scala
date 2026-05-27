package bio.domain.matrix

/** Construction failures for [[ConsensusProfileProblem]]. */
sealed trait ConsensusProfileProblemError
object ConsensusProfileProblemError {

  /** The supplied record collection was empty — a consensus/profile requires at least
    * one record to count.
    */
  case object EmptyInput extends ConsensusProfileProblemError

  /** Records were of unequal lengths. `lengths` preserves the input order so callers
    * can spot which record(s) deviated.
    */
  final case class LengthMismatch(lengths: Vector[Int]) extends ConsensusProfileProblemError
}
