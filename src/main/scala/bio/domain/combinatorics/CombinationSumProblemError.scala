package bio.domain.combinatorics

/** Construction failures for [[CombinationSumProblem]].
  *
  * Naming note: this spec's lower bound is `0` (not `1`), so the rejection cases are
  * `NegativeN` / `NegativeM` rather than the `NonPositive*` variants used by earlier
  * combinatorics specs whose lower bound was strictly positive.
  */
sealed trait CombinationSumProblemError
object CombinationSumProblemError {

  /** The supplied `n` was less than 0. Carries the offending value. */
  final case class NegativeN(value: Int) extends CombinationSumProblemError

  /** The supplied `n` exceeded the per-problem maximum (`max`). Carries the offending
    * value and the maximum.
    */
  final case class NExceedsMaximum(value: Int, max: Int) extends CombinationSumProblemError

  /** The supplied `m` was less than 0. Carries the offending value. */
  final case class NegativeM(value: Int) extends CombinationSumProblemError

  /** The cross-constraint `m <= n` was violated. Carries both offending inputs. */
  final case class MExceedsN(m: Int, n: Int) extends CombinationSumProblemError
}
