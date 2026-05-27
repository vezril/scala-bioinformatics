package bio.domain.protein

/** Construction failures for [[RnaSplicingProblem]]. */
sealed trait RnaSplicingProblemError
object RnaSplicingProblemError {

  /** The intron at position `index` (0-indexed in the input `introns` vector) was an
    * empty `DnaString`. Empty introns are degenerate and depending on the JDK can
    * produce unexpected behavior from `String.replace`; the bundle rejects them up
    * front so the algorithm can safely use literal substring replacement.
    */
  final case class EmptyIntron(index: Int) extends RnaSplicingProblemError
}
