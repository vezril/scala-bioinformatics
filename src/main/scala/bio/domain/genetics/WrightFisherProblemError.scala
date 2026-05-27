package bio.domain.genetics

/** Construction failures for [[WrightFisherProblem]].
  *
  * Each of the four integer parameters (`n`, `m`, `g`, `k`) has its own pair of
  * lower/upper-bound failure cases — 8 cases total. The `MExceedsTotalAlleles` and
  * `KExceedsTotalAlleles` names reflect the *cross-constraint* meaning ("can't have
  * more dominant/recessive alleles than `2n` total alleles") rather than a generic
  * `ExceedsMaximum`, because the maximum is a computed value (`2 * n`) rather than a
  * fixed constant.
  */
sealed trait WrightFisherProblemError
object WrightFisherProblemError {

  /** The supplied `n` was less than 1. Carries the offending value. */
  final case class NonPositiveN(value: Int) extends WrightFisherProblemError

  /** The supplied `n` exceeded the per-problem maximum (`max`). Carries the
    * offending value and the maximum.
    */
  final case class NExceedsMaximum(value: Int, max: Int) extends WrightFisherProblemError

  /** The supplied `m` was less than 1. Carries the offending value. */
  final case class NonPositiveM(value: Int) extends WrightFisherProblemError

  /** The supplied `m` exceeded the total allele count `2 * n`. Carries the offending
    * value and the computed maximum `2 * n`.
    */
  final case class MExceedsTotalAlleles(value: Int, max: Int) extends WrightFisherProblemError

  /** The supplied `g` was less than 1. Carries the offending value. */
  final case class NonPositiveG(value: Int) extends WrightFisherProblemError

  /** The supplied `g` exceeded the per-problem maximum (`max`). Carries the
    * offending value and the maximum.
    */
  final case class GExceedsMaximum(value: Int, max: Int) extends WrightFisherProblemError

  /** The supplied `k` was less than 1. Carries the offending value. */
  final case class NonPositiveK(value: Int) extends WrightFisherProblemError

  /** The supplied `k` exceeded the total allele count `2 * n`. Carries the offending
    * value and the computed maximum `2 * n`.
    */
  final case class KExceedsTotalAlleles(value: Int, max: Int) extends WrightFisherProblemError
}
