package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PopulationSpec extends AnyFunSpec with Matchers {

  describe("Population.from") {
    it("accepts a valid population (2, 2, 2) with total 6") {
      val result = Population.from(2, 2, 2)
      result shouldBe a[Right[_, _]]
      result.toOption.get.total shouldBe 6
    }

    it("accepts zero counts when total is at least 2 (2, 0, 0)") {
      val result = Population.from(2, 0, 0)
      result shouldBe a[Right[_, _]]
      result.toOption.get.total shouldBe 2
    }

    it("rejects a negative count of homozygous dominant") {
      Population.from(-1, 2, 2) shouldBe Left(PopulationError.NegativeCount)
    }

    it("rejects a negative count of heterozygous") {
      Population.from(2, -1, 2) shouldBe Left(PopulationError.NegativeCount)
    }

    it("rejects a negative count of homozygous recessive") {
      Population.from(2, 2, -1) shouldBe Left(PopulationError.NegativeCount)
    }

    it("rejects a population with total less than 2 (1, 0, 0)") {
      Population.from(1, 0, 0) shouldBe Left(PopulationError.InsufficientPopulation)
    }

    it("rejects an all-zero population (0, 0, 0)") {
      Population.from(0, 0, 0) shouldBe Left(PopulationError.InsufficientPopulation)
    }
  }

  describe("Population construction invariants") {
    it("cannot be constructed via a public companion apply (smart constructor is the only path)") {
      assertDoesNotCompile("""bio.domain.Population(2, 2, 2)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile("""bio.domain.Population.from(2, 2, 2).toOption.get.copy(homozygousDominant = 99)""")
    }
  }
}
