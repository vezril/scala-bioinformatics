package bio.domain.analysis

/** Validation errors for [[ExpectedRestrictionSitesProblem]] (Rosalind EVAL). */
sealed trait ExpectedRestrictionSitesProblemError

object ExpectedRestrictionSitesProblemError {

  /** The motif exceeds the maximum allowed length. */
  final case class MotifTooLong(length: Int, max: Int)
      extends ExpectedRestrictionSitesProblemError

  /** The motif has an odd length (Rosalind EVAL requires an even-length motif). */
  final case class OddMotifLength(length: Int) extends ExpectedRestrictionSitesProblemError

  /** The string length `n` is not a positive integer. */
  final case class NonPositiveLength(length: Int)
      extends ExpectedRestrictionSitesProblemError

  /** The string length `n` exceeds the maximum allowed. */
  final case class LengthTooLarge(length: Int, max: Int)
      extends ExpectedRestrictionSitesProblemError

  /** The GC-content array exceeds the maximum allowed size. */
  final case class TooManyGcContents(size: Int, max: Int)
      extends ExpectedRestrictionSitesProblemError
}
