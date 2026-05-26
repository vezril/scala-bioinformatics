package bio.algorithms.genetics

import bio.domain.genetics.IndependentAllelesProblem
import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class IndependentAllelesSpec extends AnyFunSpec with Matchers {

  private def prob(k: Int, n: Int): IndependentAllelesProblem =
    IndependentAllelesProblem
      .from(k, n)
      .getOrElse(sys.error(s"invalid IndependentAllelesProblem in fixture: ($k, $n)"))

  describe("IndependentAlleles.probability") {
    it("produces ~0.684 for the Rosalind sample (k=2, N=1)") {
      IndependentAlleles.probability(prob(2, 1)).value shouldBe 0.684 +- 1e-3
    }

    it("produces 7/16 for (k=1, N=1)") {
      IndependentAlleles.probability(prob(1, 1)).value shouldBe (7.0 / 16.0) +- 1e-9
    }

    it("produces 1/16 for (k=1, N=2)") {
      IndependentAlleles.probability(prob(1, 2)).value shouldBe (1.0 / 16.0) +- 1e-9
    }

    it("produces 1/256 for (k=2, N=4) — all four must be Aa Bb") {
      IndependentAlleles.probability(prob(2, 4)).value shouldBe (1.0 / 256.0) +- 1e-9
    }

    it("produces 1 - 0.75^128 for (k=7, N=1) — at least one in a population of 128") {
      IndependentAlleles.probability(prob(7, 1)).value shouldBe (1.0 - math.pow(0.75, 128.0)) +- 1e-12
    }

    it("produces 0.25^128 for (k=7, N=128) — all 128 must be Aa Bb (positive but tiny)") {
      val expected = math.pow(0.25, 128.0)
      val result   = IndependentAlleles.probability(prob(7, 128)).value
      result should be > 0.0
      result shouldBe expected +- 1e-80
    }

    it("returns a Probability with value in [0, 1]") {
      val result = IndependentAlleles.probability(prob(2, 1))
      result            shouldBe a[Probability]
      result.value should be >= 0.0
      result.value should be <= 1.0
    }
  }
}
