package bio.algorithms.combinatorics

import bio.domain.combinatorics.{SetOperationsProblem, SetOperationsResult}

import scala.collection.immutable.SortedSet

/** Computes the six derived sets of the Rosalind SETO ("Introduction to Set Operations")
  * problem from a validated [[SetOperationsProblem]].
  *
  * Over the universe `U = {1, …, n}` and subsets `A`, `B ⊆ U`, it returns the union
  * `A ∪ B`, the intersection `A ∩ B`, the directed differences `A − B` and `B − A`, and
  * the complements `Aᶜ = U − A` and `Bᶜ = U − B`. A pure, total function — every input
  * accepted by [[SetOperationsProblem.from]] has a defined result.
  */
object SetOperations {

  def compute(problem: SetOperationsProblem): SetOperationsResult = {
    val universe = SortedSet(1 to problem.n: _*)
    val a        = problem.a
    val b        = problem.b

    SetOperationsResult(
      union = universe.filter(e => a.contains(e) || b.contains(e)),
      intersection = universe.filter(e => a.contains(e) && b.contains(e)),
      aMinusB = universe.filter(e => a.contains(e) && !b.contains(e)),
      bMinusA = universe.filter(e => b.contains(e) && !a.contains(e)),
      aComplement = universe.filter(e => !a.contains(e)),
      bComplement = universe.filter(e => !b.contains(e))
    )
  }
}
