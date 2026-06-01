package bio.algorithms.combinatorics

import bio.domain.combinatorics.SetOperationsProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.SortedSet

class SetOperationsSpec extends AnyFunSpec with Matchers {

  private def problem(n: Int, a: Set[Int], b: Set[Int]): SetOperationsProblem =
    SetOperationsProblem
      .from(n, a, b)
      .getOrElse(sys.error("invalid SetOperationsProblem fixture"))

  describe("SetOperations.compute") {
    it("reproduces the canonical Rosalind sample") {
      val result = SetOperations.compute(problem(10, Set(1, 2, 3, 4, 5), Set(2, 8, 5, 10)))
      result.union shouldBe SortedSet(1, 2, 3, 4, 5, 8, 10)
      result.intersection shouldBe SortedSet(2, 5)
      result.aMinusB shouldBe SortedSet(1, 3, 4)
      result.bMinusA shouldBe SortedSet(8, 10)
      result.aComplement shouldBe SortedSet(6, 7, 8, 9, 10)
      result.bComplement shouldBe SortedSet(1, 3, 4, 6, 7, 9)
    }

    it("yields an empty intersection for disjoint subsets, with each difference the set itself") {
      val result = SetOperations.compute(problem(6, Set(1, 2), Set(3, 4)))
      result.intersection shouldBe SortedSet.empty[Int]
      result.union shouldBe SortedSet(1, 2, 3, 4)
      result.aMinusB shouldBe SortedSet(1, 2)
      result.bMinusA shouldBe SortedSet(3, 4)
    }

    it("has symmetric union/intersection but possibly asymmetric differences") {
      val ab = SetOperations.compute(problem(8, Set(1, 2, 3), Set(3, 4)))
      val ba = SetOperations.compute(problem(8, Set(3, 4), Set(1, 2, 3)))
      ab.union shouldBe ba.union
      ab.intersection shouldBe ba.intersection
      ab.aMinusB should not be ab.bMinusA
    }

    it("complements an empty subset to the whole universe") {
      val result = SetOperations.compute(problem(4, Set.empty, Set(1, 2, 3, 4)))
      result.aComplement shouldBe SortedSet(1, 2, 3, 4)
      result.bComplement shouldBe SortedSet.empty[Int]
    }
  }
}
