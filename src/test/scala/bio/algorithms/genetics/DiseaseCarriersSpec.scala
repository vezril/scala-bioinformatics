package bio.algorithms.genetics

import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DiseaseCarriersSpec extends AnyFunSpec with Matchers {

  private def prob(d: Double): Probability =
    Probability.from(d).getOrElse(sys.error(s"invalid Probability fixture: $d"))

  private val RosalindTolerance: Double = 0.001
  private val TightTolerance: Double    = 1e-12

  describe("DiseaseCarriers.frequencies") {
    it("produces the canonical Rosalind sample within 0.001 absolute error") {
      val input    = Vector(0.1, 0.25, 0.5).map(prob)
      val actual   = DiseaseCarriers.frequencies(input)
      val expected = Vector(0.532, 0.75, 0.914)
      actual.size shouldBe expected.size
      actual.zip(expected).foreach { case (a, e) =>
        a.value shouldBe e +- RosalindTolerance
      }
    }

    it("returns 0.0 for an input of 0.0 (no recessive alleles → no carriers)") {
      val result = DiseaseCarriers.frequencies(Vector(prob(0.0)))
      result.size shouldBe 1
      result(0).value shouldBe 0.0
    }

    it("returns 1.0 for an input of 1.0 (entire population homozygous recessive → all carriers)") {
      val result = DiseaseCarriers.frequencies(Vector(prob(1.0)))
      result.size shouldBe 1
      result(0).value shouldBe 1.0
    }

    it("returns exactly 0.75 for input 0.25 (the q=1/2 algebraic case, within 1e-12)") {
      val result = DiseaseCarriers.frequencies(Vector(prob(0.25)))
      result.size shouldBe 1
      result(0).value shouldBe 0.75 +- TightTolerance
    }

    it("returns an empty vector for an empty input") {
      DiseaseCarriers.frequencies(Vector.empty) shouldBe Vector.empty
    }

    it("preserves the input length (7 in → 7 out)") {
      val input = Vector(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7).map(prob)
      DiseaseCarriers.frequencies(input).size shouldBe 7
    }

    it("is monotonically non-decreasing for a sorted ascending input") {
      val input  = Vector(0.1, 0.25, 0.5, 0.9).map(prob)
      val result = DiseaseCarriers.frequencies(input)
      result.sliding(2).foreach { case Vector(a, b) =>
        a.value should be <= b.value
      }
    }

    it("every output element is a valid Probability in [0, 1]") {
      // Probability.from has already enforced [0, 1] on construction. We sample a
      // mid-range input and verify by inspection that the returned values land in range.
      val input = Vector(0.0, 0.01, 0.1, 0.25, 0.5, 0.75, 0.99, 1.0).map(prob)
      DiseaseCarriers.frequencies(input).foreach { p =>
        p.value should (be >= 0.0 and be <= 1.0)
      }
    }
  }
}
