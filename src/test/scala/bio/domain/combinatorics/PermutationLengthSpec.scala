package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PermutationLengthSpec extends AnyFunSpec with Matchers {

  describe("PermutationLength.from") {
    it("accepts the lower-bound value 1") {
      val result = PermutationLength.from(1).toOption.get
      result.value shouldBe 1
    }

    it("accepts the Rosalind sample value 3") {
      val result = PermutationLength.from(3).toOption.get
      result.value shouldBe 3
    }

    it("accepts the upper-bound value 7") {
      val result = PermutationLength.from(7).toOption.get
      result.value shouldBe 7
    }

    it("rejects zero as NonPositive") {
      PermutationLength.from(0) shouldBe Left(PermutationLengthError.NonPositive(0))
    }

    it("rejects a negative value as NonPositive") {
      PermutationLength.from(-1) shouldBe Left(PermutationLengthError.NonPositive(-1))
    }

    it("rejects value 8 as ExceedsMaximum(8, 7)") {
      PermutationLength.from(8) shouldBe Left(PermutationLengthError.ExceedsMaximum(8, 7))
    }
  }

  describe("PermutationLength construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.combinatorics.PermutationLength(3)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.PermutationLength.from(3).toOption.get.copy(value = 99)"""
      )
    }
  }
}
