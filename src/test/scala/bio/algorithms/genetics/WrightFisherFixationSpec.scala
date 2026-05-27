package bio.algorithms.genetics

import bio.domain.genetics.WrightFisherFixationProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WrightFisherFixationSpec extends AnyFunSpec with Matchers {

  private def problem(n: Int, m: Int, recessive: Vector[Int]): WrightFisherFixationProblem =
    WrightFisherFixationProblem
      .from(n, m, recessive)
      .getOrElse(sys.error(s"invalid WrightFisherFixationProblem fixture (n=$n, m=$m, A=$recessive)"))

  private val TightTolerance: Double = 1e-9

  describe("WrightFisher.fixationLogProbs") {
    it("produces the canonical 3×3 Rosalind sample matrix within 1e-9 per element") {
      val result = WrightFisher.fixationLogProbs(problem(4, 3, Vector(0, 1, 2)))
      val expected = Vector(
        Vector(0.0, -0.463935575821, -0.999509892866),
        Vector(0.0, -0.301424998891, -0.641668367342),
        Vector(0.0, -0.229066698008, -0.485798552456)
      )
      result.size shouldBe 3
      result.zip(expected).foreach { case (actualRow, expectedRow) =>
        actualRow.size shouldBe expectedRow.size
        actualRow.zip(expectedRow).foreach { case (a, e) =>
          a shouldBe e +- TightTolerance
        }
      }
    }

    it("yields 0.0 at every generation when A[j] = 0 (already fixed)") {
      val result = WrightFisher.fixationLogProbs(problem(4, 5, Vector(0)))
      result.foreach { row => row(0) shouldBe 0.0 }
    }

    it("yields Double.NegativeInfinity when A[j] = 2n (all-recessive absorbing state)") {
      val result = WrightFisher.fixationLogProbs(problem(1, 1, Vector(2)))
      result(0)(0) shouldBe Double.NegativeInfinity
    }

    it("matches the analytic spot-check log10(0.25) for N=1, m=1, A=[1]") {
      val result   = WrightFisher.fixationLogProbs(problem(1, 1, Vector(1)))
      val expected = Math.log10(0.25)
      result(0)(0) shouldBe expected +- TightTolerance
    }

    it("produces output dimensions m × |recessiveCounts| (m=5, |A|=7 → 5×7)") {
      val result = WrightFisher.fixationLogProbs(problem(4, 5, Vector(0, 1, 2, 3, 4, 5, 6)))
      result.size shouldBe 5
      result.foreach { row => row.size shouldBe 7 }
    }

    it("produces m empty inner vectors when recessiveCounts is empty") {
      val result = WrightFisher.fixationLogProbs(problem(4, 3, Vector.empty))
      result.size shouldBe 3
      result.foreach { row => row shouldBe Vector.empty }
    }

    it("is monotonically non-decreasing in generation for a single non-degenerate factor") {
      // A=[1], n=4, m=3: fixation prob can only grow as generations pass.
      val result = WrightFisher.fixationLogProbs(problem(4, 3, Vector(1)))
      result.sliding(2).foreach { case Vector(a, b) =>
        a(0) should be <= b(0)
      }
    }
  }
}
