package bio.domain.graph

/** Construction failures for [[SplitDistanceProblem]] (Rosalind SPTD). */
sealed trait SplitDistanceProblemError
object SplitDistanceProblemError {

  /** No taxon names were supplied. */
  case object EmptyTaxa extends SplitDistanceProblemError

  /** A taxon name occurred more than once. */
  final case class DuplicateTaxon(name: String) extends SplitDistanceProblemError

  /** A tree's set of leaf labels differed from the taxa set.
    *
    * @param treeIndex 1-based tree number (`1` for `T1`, `2` for `T2`)
    * @param missing   taxa absent from the tree's leaves
    * @param extra     leaf labels not present in the taxa
    */
  final case class TreeTaxaMismatch(
      treeIndex: Int,
      missing: Set[String],
      extra: Set[String]
  ) extends SplitDistanceProblemError
}
