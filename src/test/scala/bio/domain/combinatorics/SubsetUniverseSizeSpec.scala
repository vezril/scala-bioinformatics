package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SubsetUniverseSizeSpec extends AnyFunSpec with Matchers {

  describe("SubsetUniverseSize.from") {
    it("accepts the minimum value (n=1)") {
      SubsetUniverseSize.from(1).map(_.value) shouldBe Right(1)
    }

    it("accepts the Rosalind sample value (n=3)") {
      SubsetUniverseSize.from(3).map(_.value) shouldBe Right(3)
    }

    it("accepts the upper bound (n=1000)") {
      SubsetUniverseSize.from(1000).map(_.value) shouldBe Right(1000)
    }

    it("rejects n = 0 as NonPositive(0)") {
      SubsetUniverseSize.from(0) shouldBe Left(SubsetUniverseSizeError.NonPositive(0))
    }

    it("rejects a negative n as NonPositive") {
      SubsetUniverseSize.from(-5) shouldBe Left(SubsetUniverseSizeError.NonPositive(-5))
    }

    it("rejects n = 1001 as ExceedsMaximum(1001, 1000)") {
      SubsetUniverseSize.from(1001) shouldBe
        Left(SubsetUniverseSizeError.ExceedsMaximum(1001, 1000))
    }

    it("validates the lower bound before the upper bound") {
      SubsetUniverseSize.from(0) shouldBe Left(SubsetUniverseSizeError.NonPositive(0))
    }
  }

  describe("SubsetUniverseSize construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.combinatorics.SubsetUniverseSize(3)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.SubsetUniverseSize.from(3).toOption.get.copy(value = 7)"""
      )
    }
  }
}
