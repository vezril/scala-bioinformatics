package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ChromosomePairsSpec extends AnyFunSpec with Matchers {

  describe("ChromosomePairs.from") {
    it("accepts the minimum value (n=1)") {
      ChromosomePairs.from(1).map(_.value) shouldBe Right(1)
    }

    it("accepts the Rosalind sample value (n=5)") {
      ChromosomePairs.from(5).map(_.value) shouldBe Right(5)
    }

    it("accepts the upper bound (n=50)") {
      ChromosomePairs.from(50).map(_.value) shouldBe Right(50)
    }

    it("rejects n = 0 as NonPositive(0)") {
      ChromosomePairs.from(0) shouldBe Left(ChromosomePairsError.NonPositive(0))
    }

    it("rejects a negative n as NonPositive") {
      ChromosomePairs.from(-3) shouldBe Left(ChromosomePairsError.NonPositive(-3))
    }

    it("rejects n = 51 as ExceedsMaximum(51, 50)") {
      ChromosomePairs.from(51) shouldBe Left(ChromosomePairsError.ExceedsMaximum(51, 50))
    }

    it("validates the lower bound before the upper bound") {
      ChromosomePairs.from(0) shouldBe Left(ChromosomePairsError.NonPositive(0))
    }
  }

  describe("ChromosomePairs construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.genetics.ChromosomePairs(5)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.genetics.ChromosomePairs.from(5).toOption.get.copy(value = 7)"""
      )
    }
  }
}
