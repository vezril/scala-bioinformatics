package bio.domain.combinatorics

/** The size `n` of the universe set `{1, 2, ..., n}` whose subsets are being counted
  * by the [[bio.algorithms.combinatorics.Subsets]] algorithm (Rosalind SSET).
  *
  * Constructable only via [[SubsetUniverseSize.from]] which enforces:
  *   - `value >= 1` (Rosalind problem: "positive integer")
  *   - `value <= 1000` (Rosalind upper bound)
  *
  * Validation order: lower bound, then upper bound. First failure wins.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor. Mirrors the framework's existing
  * single-parameter validated wrappers (`PermutationLength`, `OverlapLength`).
  */
sealed abstract case class SubsetUniverseSize(value: Int)

object SubsetUniverseSize {
  private val MaxN: Int = 1000

  def from(value: Int): Either[SubsetUniverseSizeError, SubsetUniverseSize] =
    if (value < 1) Left(SubsetUniverseSizeError.NonPositive(value))
    else if (value > MaxN) Left(SubsetUniverseSizeError.ExceedsMaximum(value, MaxN))
    else Right(new SubsetUniverseSize(value) {})
}
