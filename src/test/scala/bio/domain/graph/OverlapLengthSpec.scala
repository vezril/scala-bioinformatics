package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OverlapLengthSpec extends AnyFunSpec with Matchers {

  describe("OverlapLength.from") {
    it("accepts k = 3 (the Rosalind O_3 case)") {
      OverlapLength.from(3).map(_.value) shouldBe Right(3)
    }

    it("accepts k = 1 (the minimum positive value)") {
      OverlapLength.from(1).map(_.value) shouldBe Right(1)
    }

    it("rejects k = 0 as NonPositive(0)") {
      OverlapLength.from(0) shouldBe Left(OverlapLengthError.NonPositive(0))
    }

    it("rejects negative k as NonPositive") {
      OverlapLength.from(-5) shouldBe Left(OverlapLengthError.NonPositive(-5))
    }
  }

  describe("OverlapLength construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.graph.OverlapLength(3)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.OverlapLength.from(3).toOption.get.copy(value = 7)"""
      )
    }
  }
}
