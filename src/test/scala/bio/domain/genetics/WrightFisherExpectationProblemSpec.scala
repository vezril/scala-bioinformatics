package bio.domain.genetics

import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WrightFisherExpectationProblemSpec extends AnyFunSpec with Matchers {

  private def prob(d: Double): Probability =
    Probability.from(d).getOrElse(sys.error(s"invalid Probability fixture: $d"))

  describe("WrightFisherExpectationProblem.from") {
    it("accepts the Rosalind sample parameters (n=17, p=[0.1, 0.2, 0.3])") {
      val p       = Vector(0.1, 0.2, 0.3).map(prob)
      val problem = WrightFisherExpectationProblem.from(17, p).toOption.get
      problem.n shouldBe 17
      problem.p shouldBe p
    }

    it("accepts the minimum n=1 with a single-element p") {
      val problem = WrightFisherExpectationProblem.from(1, Vector(prob(0.5))).toOption.get
      problem.n shouldBe 1
      problem.p.size shouldBe 1
    }

    it("accepts the upper-bound n=1,000,000 with an empty p") {
      WrightFisherExpectationProblem.from(1_000_000, Vector.empty) shouldBe a[Right[_, _]]
    }

    it("accepts the upper-bound p size (20)") {
      val p = Vector.fill(20)(prob(0.5))
      WrightFisherExpectationProblem.from(17, p) shouldBe a[Right[_, _]]
    }

    it("accepts an empty p vector") {
      val problem = WrightFisherExpectationProblem.from(17, Vector.empty).toOption.get
      problem.p shouldBe Vector.empty
    }

    it("rejects non-positive n as NonPositiveN") {
      WrightFisherExpectationProblem.from(0, Vector.empty) shouldBe
        Left(WrightFisherExpectationProblemError.NonPositiveN(0))
    }

    it("rejects n exceeding 1,000,000 as NExceedsMaximum") {
      WrightFisherExpectationProblem.from(1_000_001, Vector.empty) shouldBe
        Left(WrightFisherExpectationProblemError.NExceedsMaximum(1_000_001, 1_000_000))
    }

    it("rejects p with more than 20 elements as TooManyProbabilities") {
      val p = Vector.fill(21)(prob(0.5))
      WrightFisherExpectationProblem.from(17, p) shouldBe
        Left(WrightFisherExpectationProblemError.TooManyProbabilities(21, 20))
    }

    it("validates n lower bound before n upper bound") {
      val p = Vector.fill(21)(prob(0.5))
      WrightFisherExpectationProblem.from(0, p) shouldBe
        Left(WrightFisherExpectationProblemError.NonPositiveN(0))
    }

    it("validates n upper bound before p.size check") {
      val p = Vector.fill(21)(prob(0.5))
      WrightFisherExpectationProblem.from(1_000_001, p) shouldBe
        Left(WrightFisherExpectationProblemError.NExceedsMaximum(1_000_001, 1_000_000))
    }
  }

  describe("WrightFisherExpectationProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.genetics.WrightFisherExpectationProblem(17, Vector.empty)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.genetics.WrightFisherExpectationProblem
          |  .from(17, Vector.empty).toOption.get.copy(n = 99)""".stripMargin
      )
    }
  }
}
