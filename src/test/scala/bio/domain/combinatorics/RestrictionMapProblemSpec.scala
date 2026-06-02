package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RestrictionMapProblemSpec extends AnyFunSpec with Matchers {

  describe("RestrictionMapProblem.from") {
    it("accepts a valid distance multiset, preserving the distances") {
      val l = Vector(2, 2, 3, 3, 4, 5, 6, 7, 8, 10)
      RestrictionMapProblem.from(l).map(_.distances) shouldBe Right(l)
    }

    it("accepts an empty multiset") {
      RestrictionMapProblem.from(Vector.empty).isRight shouldBe true
    }

    it("accepts a single-distance multiset") {
      RestrictionMapProblem.from(Vector(5)).isRight shouldBe true
    }

    it("rejects a multiset whose size is not triangular") {
      RestrictionMapProblem.from(Vector(2, 3)) shouldBe Left(
        RestrictionMapProblemError.InvalidSize(2)
      )
    }

    it("rejects a non-positive distance") {
      RestrictionMapProblem.from(Vector(2, -1, 3)) shouldBe Left(
        RestrictionMapProblemError.NonPositiveDistance(1, -1)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.RestrictionMapProblem(Vector(5))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.RestrictionMapProblem.from(Vector(5)).toOption.get.copy()"""
      )
    }
  }
}
