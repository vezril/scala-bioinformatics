package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WrightFisherProblemSpec extends AnyFunSpec with Matchers {

  describe("WrightFisherProblem.from") {
    it("accepts the Rosalind sample (4, 6, 2, 1)") {
      val problem = WrightFisherProblem.from(4, 6, 2, 1).toOption.get
      problem.n shouldBe 4
      problem.m shouldBe 6
      problem.g shouldBe 2
      problem.k shouldBe 1
    }

    it("accepts the minimum bounds (1, 1, 1, 1)") {
      val problem = WrightFisherProblem.from(1, 1, 1, 1).toOption.get
      problem.n shouldBe 1
      problem.m shouldBe 1
      problem.g shouldBe 1
      problem.k shouldBe 1
    }

    it("accepts the upper bounds (7, 14, 6, 14)") {
      val problem = WrightFisherProblem.from(7, 14, 6, 14).toOption.get
      problem.n shouldBe 7
      problem.m shouldBe 14
      problem.g shouldBe 6
      problem.k shouldBe 14
    }

    it("accepts m at the 2n boundary (4, 8, 1, 1)") {
      val problem = WrightFisherProblem.from(4, 8, 1, 1).toOption.get
      problem.m shouldBe 8
    }

    it("rejects non-positive n as NonPositiveN") {
      WrightFisherProblem.from(0, 1, 1, 1) shouldBe
        Left(WrightFisherProblemError.NonPositiveN(0))
    }

    it("rejects n > 7 as NExceedsMaximum") {
      WrightFisherProblem.from(8, 1, 1, 1) shouldBe
        Left(WrightFisherProblemError.NExceedsMaximum(8, 7))
    }

    it("rejects non-positive m as NonPositiveM") {
      WrightFisherProblem.from(4, 0, 1, 1) shouldBe
        Left(WrightFisherProblemError.NonPositiveM(0))
    }

    it("rejects m > 2n as MExceedsTotalAlleles") {
      WrightFisherProblem.from(4, 9, 1, 1) shouldBe
        Left(WrightFisherProblemError.MExceedsTotalAlleles(9, 8))
    }

    it("rejects non-positive g as NonPositiveG") {
      WrightFisherProblem.from(4, 1, 0, 1) shouldBe
        Left(WrightFisherProblemError.NonPositiveG(0))
    }

    it("rejects g > 6 as GExceedsMaximum") {
      WrightFisherProblem.from(4, 1, 7, 1) shouldBe
        Left(WrightFisherProblemError.GExceedsMaximum(7, 6))
    }

    it("rejects non-positive k as NonPositiveK") {
      WrightFisherProblem.from(4, 1, 1, 0) shouldBe
        Left(WrightFisherProblemError.NonPositiveK(0))
    }

    it("rejects k > 2n as KExceedsTotalAlleles") {
      WrightFisherProblem.from(4, 1, 1, 9) shouldBe
        Left(WrightFisherProblemError.KExceedsTotalAlleles(9, 8))
    }

    it("validates n lower bound first (all invalid)") {
      WrightFisherProblem.from(0, 0, 0, 0) shouldBe
        Left(WrightFisherProblemError.NonPositiveN(0))
    }

    it("validates n upper bound before m/g/k checks") {
      WrightFisherProblem.from(8, 0, 0, 0) shouldBe
        Left(WrightFisherProblemError.NExceedsMaximum(8, 7))
    }

    it("validates m upper bound before g/k checks") {
      WrightFisherProblem.from(4, 9, 0, 0) shouldBe
        Left(WrightFisherProblemError.MExceedsTotalAlleles(9, 8))
    }

    it("validates g upper bound before k checks") {
      WrightFisherProblem.from(4, 1, 7, 0) shouldBe
        Left(WrightFisherProblemError.GExceedsMaximum(7, 6))
    }
  }

  describe("WrightFisherProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.genetics.WrightFisherProblem(4, 6, 2, 1)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.genetics.WrightFisherProblem
          |  .from(4, 6, 2, 1).toOption.get.copy(n = 99)""".stripMargin
      )
    }
  }
}
