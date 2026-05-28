package bio.domain.nucleic

/** Construction failures for [[NoncrossingMatchingProblem]]. */
sealed trait NoncrossingMatchingProblemError
object NoncrossingMatchingProblemError {

  /** The RNA string exceeded the Rosalind CAT cap of 300 nt. */
  final case class ExceedsMaxLength(length: Int, max: Int)
      extends NoncrossingMatchingProblemError

  /** The A and U symbol counts were not equal — no perfect basepair matching
    * (noncrossing or otherwise) can pair every A with a U.
    */
  final case class UnpairedAU(aCount: Int, uCount: Int)
      extends NoncrossingMatchingProblemError

  /** The C and G symbol counts were not equal — no perfect basepair matching
    * (noncrossing or otherwise) can pair every C with a G.
    */
  final case class UnpairedCG(cCount: Int, gCount: Int)
      extends NoncrossingMatchingProblemError
}
