package bio.algorithms.genetics

import bio.domain.genetics.WrightFisherExpectationProblem
import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WrightFisherExpectationSpec extends AnyFunSpec with Matchers {

  private def prob(d: Double): Probability =
    Probability.from(d).getOrElse(sys.error(s"invalid Probability fixture: $d"))

  private def problem(n: Int, ps: Vector[Double]): WrightFisherExpectationProblem =
    WrightFisherExpectationProblem
      .from(n, ps.map(prob))
      .getOrElse(sys.error(s"invalid WrightFisherExpectationProblem fixture (n=$n, ps=$ps)"))

  private val RosalindTolerance: Double = 0.001

  describe("WrightFisher.expectedFrequencies") {
    it("produces [1.7, 3.4, 5.1] for the Rosalind sample (n=17, P=[0.1, 0.2, 0.3]) within 0.001") {
      val actual   = WrightFisher.expectedFrequencies(problem(17, Vector(0.1, 0.2, 0.3)))
      val expected = Vector(1.7, 3.4, 5.1)
      actual.size shouldBe expected.size
      actual.zip(expected).foreach { case (a, e) =>
        a shouldBe e +- RosalindTolerance
      }
    }

    it("returns an empty vector when p is empty") {
      WrightFisher.expectedFrequencies(problem(17, Vector.empty)) shouldBe Vector.empty
    }

    it("yields 0.0 for p=[0.0] regardless of n") {
      WrightFisher.expectedFrequencies(problem(1000, Vector(0.0))) shouldBe Vector(0.0)
    }

    it("yields n exactly for p=[1.0]") {
      WrightFisher.expectedFrequencies(problem(1000, Vector(1.0))) shouldBe Vector(1000.0)
    }

    it("yields 500000.0 exactly for the upper-bound (n=1,000,000, p=[0.5])") {
      WrightFisher.expectedFrequencies(problem(1_000_000, Vector(0.5))) shouldBe Vector(500000.0)
    }

    it("preserves the input length (p.size=20 → result.size=20)") {
      val ps = Vector.fill(20)(0.5)
      WrightFisher.expectedFrequencies(problem(100, ps)).size shouldBe 20
    }

    it("is monotonically non-decreasing for sorted ascending p") {
      val result = WrightFisher.expectedFrequencies(problem(100, Vector(0.1, 0.3, 0.5, 0.7, 0.9)))
      result.sliding(2).foreach { case Vector(a, b) =>
        a should be <= b
      }
    }
  }
}
