package bio.domain.nucleic

/** Construction failures for [[PerfectMatchingProblem]]. */
sealed trait PerfectMatchingProblemError
object PerfectMatchingProblemError {

  /** The RNA string exceeded the Rosalind PMCH cap of 80 nt. */
  final case class ExceedsMaxLength(length: Int, max: Int)
      extends PerfectMatchingProblemError

  /** The A and U symbol counts were not equal — no perfect basepair matching
    * can pair every A with a U.
    */
  final case class UnpairedAU(aCount: Int, uCount: Int)
      extends PerfectMatchingProblemError

  /** The C and G symbol counts were not equal — no perfect basepair matching
    * can pair every C with a G.
    */
  final case class UnpairedCG(cCount: Int, gCount: Int)
      extends PerfectMatchingProblemError
}
