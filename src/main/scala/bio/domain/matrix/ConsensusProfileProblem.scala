package bio.domain.matrix

import bio.parsing.FastaRecord

/** Validated input bundle for the consensus-and-profile algorithm.
  *
  * Constructable only via [[ConsensusProfileProblem.from]] which enforces:
  *   - `records` is non-empty
  *   - every record's `dna.value.length` equals the first record's
  *
  * Validation order: empty-check first, then length-mismatch. First failure wins —
  * an empty input is reported as [[ConsensusProfileProblemError.EmptyInput]] rather
  * than degenerating into a `LengthMismatch(Vector.empty)`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class ConsensusProfileProblem(records: Vector[FastaRecord])

object ConsensusProfileProblem {

  def from(
      records: Vector[FastaRecord]
  ): Either[ConsensusProfileProblemError, ConsensusProfileProblem] =
    if (records.isEmpty) Left(ConsensusProfileProblemError.EmptyInput)
    else {
      val lengths = records.map(_.dna.value.length)
      if (lengths.distinct.size > 1)
        Left(ConsensusProfileProblemError.LengthMismatch(lengths))
      else
        Right(new ConsensusProfileProblem(records) {})
    }
}
