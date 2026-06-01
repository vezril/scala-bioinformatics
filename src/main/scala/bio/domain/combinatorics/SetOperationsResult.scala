package bio.domain.combinatorics

import scala.collection.immutable.SortedSet

/** The six derived sets of the Rosalind SETO problem, produced by
  * [[bio.algorithms.combinatorics.SetOperations.compute]].
  *
  * Each set is stored as a [[scala.collection.immutable.SortedSet]] so [[format]] is
  * deterministic. Rosalind grades set membership rather than element order, so ascending
  * order is a safe canonical rendering.
  *
  * @param union        `A ∪ B`
  * @param intersection `A ∩ B`
  * @param aMinusB      `A − B`
  * @param bMinusA      `B − A`
  * @param aComplement  `Aᶜ = {1, …, n} − A`
  * @param bComplement  `Bᶜ = {1, …, n} − B`
  */
final case class SetOperationsResult(
    union: SortedSet[Int],
    intersection: SortedSet[Int],
    aMinusB: SortedSet[Int],
    bMinusA: SortedSet[Int],
    aComplement: SortedSet[Int],
    bComplement: SortedSet[Int]
) {

  /** The six sets, each on its own line, rendered as `{e1, e2, …}` in ascending order
    * (an empty set as `{}`).
    */
  def format: String =
    List(union, intersection, aMinusB, bMinusA, aComplement, bComplement)
      .map(renderSet)
      .mkString("\n")

  private def renderSet(set: SortedSet[Int]): String =
    set.mkString("{", ", ", "}")
}
