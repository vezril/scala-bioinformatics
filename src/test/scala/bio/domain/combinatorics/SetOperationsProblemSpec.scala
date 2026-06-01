package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SetOperationsProblemSpec extends AnyFunSpec with Matchers {

  describe("SetOperationsProblem.from") {
    it("accepts the canonical Rosalind sample input") {
      val result = SetOperationsProblem.from(10, Set(1, 2, 3, 4, 5), Set(2, 8, 5, 10))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.n shouldBe 10
      problem.a shouldBe Set(1, 2, 3, 4, 5)
      problem.b shouldBe Set(2, 8, 5, 10)
    }

    it("accepts empty subsets") {
      SetOperationsProblem.from(5, Set.empty, Set.empty).isRight shouldBe true
    }

    it("rejects a non-positive universe as NonPositiveUniverse") {
      SetOperationsProblem.from(0, Set.empty, Set.empty) shouldBe
        Left(SetOperationsProblemError.NonPositiveUniverse(0))
    }

    it("rejects a universe above the maximum as ExceedsMaximum") {
      SetOperationsProblem.from(20001, Set.empty, Set.empty) shouldBe
        Left(SetOperationsProblemError.ExceedsMaximum(20001, 20000))
    }

    it("rejects an element of A outside the universe") {
      SetOperationsProblem.from(5, Set(1, 6), Set(2)) shouldBe
        Left(SetOperationsProblemError.ElementOutOfRange("A", 6, 5))
    }

    it("rejects an element of B outside the universe (zero is out of range)") {
      SetOperationsProblem.from(5, Set(1), Set(0, 3)) shouldBe
        Left(SetOperationsProblemError.ElementOutOfRange("B", 0, 5))
    }

    it("reports the smallest offending element in the offending set") {
      SetOperationsProblem.from(5, Set(9, 7), Set(2)) shouldBe
        Left(SetOperationsProblemError.ElementOutOfRange("A", 7, 5))
    }

    it("checks the universe before element ranges (first-failure-wins)") {
      SetOperationsProblem.from(0, Set(99), Set(99)) shouldBe
        Left(SetOperationsProblemError.NonPositiveUniverse(0))
    }

    it("checks subset A before subset B") {
      SetOperationsProblem.from(5, Set(6), Set(7)) shouldBe
        Left(SetOperationsProblemError.ElementOutOfRange("A", 6, 5))
    }
  }

  describe("SetOperationsProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.SetOperationsProblem(10, Set(1), Set(2))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.SetOperationsProblem
          |  .from(10, Set(1), Set(2))
          |  .toOption.get.copy(n = 5)""".stripMargin
      )
    }
  }
}
