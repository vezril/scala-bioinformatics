package bio.domain.stats

/** A probability value, validated to be a finite Double in `[0.0, 1.0]`.
  * Constructable only via [[Probability.from]] (returns `Either`) or [[Probability.unsafeFrom]]
  * (internal use only, bypasses validation).
  */
final class Probability private (val value: Double) extends AnyVal

object Probability {
  def from(d: Double): Either[ProbabilityError, Probability] =
    if (d.isNaN || d.isInfinite)
      Left(ProbabilityError.NotFinite)
    else if (d < 0.0 || d > 1.0)
      Left(ProbabilityError.OutOfRange(d))
    else
      Right(new Probability(d))

  /** Internal constructor for trusted callers that guarantee `d` is finite and in `[0, 1]`.
    * Bypasses validation. Use [[from]] for any externally-sourced input.
    */
  private[bio] def unsafeFrom(d: Double): Probability = new Probability(d)
}
