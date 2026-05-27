package bio.algorithms.genetics

import bio.domain.genetics.WrightFisherProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WrightFisherSpec extends AnyFunSpec with Matchers {

  private def problem(n: Int, m: Int, g: Int, k: Int): WrightFisherProblem =
    WrightFisherProblem
      .from(n, m, g, k)
      .getOrElse(sys.error(s"invalid WrightFisherProblem fixture ($n, $m, $g, $k)"))

  private val RosalindTolerance: Double = 0.001
  private val TightTolerance: Double    = 1e-9

  describe("WrightFisher.atLeast") {
    it("produces 0.7717925 for the Rosalind sample (n=4, m=6, g=2, k=1) within 0.001") {
      WrightFisher.atLeast(problem(4, 6, 2, 1)).value shouldBe 0.7717925 +- RosalindTolerance
    }

    it("returns exactly 0.0 for the all-dominant absorbing state (n=4, m=8, g=1, k=1)") {
      // m == 2n means p == 1 in the first transition → 0 recessive with probability 1.
      WrightFisher.atLeast(problem(4, 8, 1, 1)).value shouldBe 0.0
    }

    it("returns 1 - (3/4)^4 for the analytic spot-check (n=2, m=3, g=1, k=1) within 1e-9") {
      // 2N=4, m=3 dominant → p=0.75. After 1 generation, P(at least 1 recessive) = 1 - P(all 4 dom) = 1 - 0.75^4.
      val expected = 1.0 - Math.pow(0.75, 4.0)
      WrightFisher.atLeast(problem(2, 3, 1, 1)).value shouldBe expected +- TightTolerance
    }

    it("is monotonically non-increasing in k (the tail shrinks as the threshold grows)") {
      val r1 = WrightFisher.atLeast(problem(4, 6, 2, 1)).value
      val r2 = WrightFisher.atLeast(problem(4, 6, 2, 2)).value
      r1 should be >= r2
    }

    it("produces 0.5385700 for the upper-bound parameters (n=7, m=7, g=6, k=7) within 0.001") {
      val result = WrightFisher.atLeast(problem(7, 7, 6, 7))
      result.value shouldBe 0.5385700 +- RosalindTolerance
      result.value should (be >= 0.0 and be <= 1.0)
    }

    it("every output is a valid Probability in [0, 1] (sampling several problems)") {
      val sample = Vector(
        problem(1, 1, 1, 1),
        problem(2, 2, 3, 2),
        problem(4, 6, 2, 1),
        problem(5, 3, 4, 5),
        problem(7, 7, 6, 7)
      )
      sample.foreach { p =>
        val v = WrightFisher.atLeast(p).value
        v should (be >= 0.0 and be <= 1.0)
      }
    }
  }
}
