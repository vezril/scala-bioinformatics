package bio.algorithms.genetics

import bio.domain.genetics.CouplePopulation
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ExpectedOffspringSpec extends AnyFunSpec with Matchers {

  private val Tolerance: Double = 1e-9

  private def pop(c1: Int, c2: Int, c3: Int, c4: Int, c5: Int, c6: Int): CouplePopulation =
    CouplePopulation
      .from(c1, c2, c3, c4, c5, c6)
      .getOrElse(sys.error("invalid CouplePopulation in test fixture"))

  describe("ExpectedOffspring.dominantPhenotype") {
    it("produces 3.5 for the Rosalind sample (1, 0, 0, 1, 0, 1)") {
      ExpectedOffspring.dominantPhenotype(pop(1, 0, 0, 1, 0, 1)) shouldBe 3.5 +- Tolerance
    }

    it("produces 0.0 for an all-zero population") {
      ExpectedOffspring.dominantPhenotype(pop(0, 0, 0, 0, 0, 0)) shouldBe 0.0
    }

    it("produces 2.0 for a single AA × AA couple") {
      ExpectedOffspring.dominantPhenotype(pop(1, 0, 0, 0, 0, 0)) shouldBe 2.0 +- Tolerance
    }

    it("produces 2.0 for a single AA × Aa couple") {
      ExpectedOffspring.dominantPhenotype(pop(0, 1, 0, 0, 0, 0)) shouldBe 2.0 +- Tolerance
    }

    it("produces 2.0 for a single AA × aa couple") {
      ExpectedOffspring.dominantPhenotype(pop(0, 0, 1, 0, 0, 0)) shouldBe 2.0 +- Tolerance
    }

    it("produces 1.5 for a single Aa × Aa couple") {
      ExpectedOffspring.dominantPhenotype(pop(0, 0, 0, 1, 0, 0)) shouldBe 1.5 +- Tolerance
    }

    it("produces 1.0 for a single Aa × aa couple") {
      ExpectedOffspring.dominantPhenotype(pop(0, 0, 0, 0, 1, 0)) shouldBe 1.0 +- Tolerance
    }

    it("produces 0.0 for any number of aa × aa couples") {
      ExpectedOffspring.dominantPhenotype(pop(0, 0, 0, 0, 0, 100)) shouldBe 0.0
    }

    it("produces 170000.0 for the upper-boundary population (20000 ×6)") {
      ExpectedOffspring.dominantPhenotype(
        pop(20000, 20000, 20000, 20000, 20000, 20000)
      ) shouldBe 170000.0 +- 1e-6
    }
  }
}
