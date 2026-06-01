package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LeafCountSpec extends AnyFunSpec with Matchers {

  describe("LeafCount.from") {
    it("accepts the minimum value (n=1)") {
      LeafCount.from(1).map(_.value) shouldBe Right(1)
    }

    it("accepts the Rosalind sample value (n=5)") {
      LeafCount.from(5).map(_.value) shouldBe Right(5)
    }

    it("accepts the upper bound (n=1000)") {
      LeafCount.from(1000).map(_.value) shouldBe Right(1000)
    }

    it("rejects n = 0 as NonPositive(0)") {
      LeafCount.from(0) shouldBe Left(LeafCountError.NonPositive(0))
    }

    it("rejects a negative n as NonPositive") {
      LeafCount.from(-5) shouldBe Left(LeafCountError.NonPositive(-5))
    }

    it("rejects n = 1001 as ExceedsMaximum(1001, 1000)") {
      LeafCount.from(1001) shouldBe Left(LeafCountError.ExceedsMaximum(1001, 1000))
    }

    it("validates the lower bound before the upper bound") {
      LeafCount.from(0) shouldBe Left(LeafCountError.NonPositive(0))
    }
  }

  describe("LeafCount construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.combinatorics.LeafCount(5)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.LeafCount.from(5).toOption.get.copy(value = 7)"""
      )
    }
  }
}
