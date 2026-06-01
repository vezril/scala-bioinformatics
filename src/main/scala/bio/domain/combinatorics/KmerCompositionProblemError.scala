package bio.domain.combinatorics

/** Construction failures for [[KmerCompositionProblem]].
  *
  * DNA-string validity is handled upstream by `DnaString`, so only the word
  * length `k` is validated here.
  */
sealed trait KmerCompositionProblemError
object KmerCompositionProblemError {

  /** The requested word length was not positive. */
  final case class NonPositiveK(k: Int) extends KmerCompositionProblemError

  /** The requested word length exceeded the Rosalind KMER cap of 10. */
  final case class KExceedsMaximum(k: Int, max: Int) extends KmerCompositionProblemError
}
