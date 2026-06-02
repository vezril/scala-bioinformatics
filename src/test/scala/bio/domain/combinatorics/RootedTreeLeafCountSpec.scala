package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RootedTreeLeafCountSpec extends AnyFunSpec with Matchers {

  describe("RootedTreeLeafCount.from") {
    it("accepts a value within bounds, exposing it") {
      RootedTreeLeafCount.from(4).map(_.value) shouldBe Right(4)
    }

    it("accepts the lower bound of one") {
      RootedTreeLeafCount.from(1).isRight shouldBe true
    }

    it("accepts the upper bound of 1000") {
      RootedTreeLeafCount.from(1000).isRight shouldBe true
    }

    it("rejects a non-positive value") {
      RootedTreeLeafCount.from(0) shouldBe Left(RootedTreeLeafCountError.NonPositive(0))
    }

    it("rejects a value above the maximum") {
      RootedTreeLeafCount.from(1001) shouldBe Left(
        RootedTreeLeafCountError.ExceedsMaximum(1001, 1000)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.combinatorics.RootedTreeLeafCount(4)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.RootedTreeLeafCount.from(4).toOption.get.copy(value = 99)"""
      )
    }
  }
}
