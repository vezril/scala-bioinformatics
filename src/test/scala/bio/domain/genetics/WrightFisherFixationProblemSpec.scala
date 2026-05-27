package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WrightFisherFixationProblemSpec extends AnyFunSpec with Matchers {

  describe("WrightFisherFixationProblem.from") {
    it("accepts the Rosalind sample (4, 3, [0, 1, 2])") {
      val problem = WrightFisherFixationProblem.from(4, 3, Vector(0, 1, 2)).toOption.get
      problem.n shouldBe 4
      problem.m shouldBe 3
      problem.recessiveCounts shouldBe Vector(0, 1, 2)
    }

    it("accepts the minimum bounds (n=1, m=1, empty recessiveCounts)") {
      WrightFisherFixationProblem.from(1, 1, Vector.empty) shouldBe a[Right[_, _]]
    }

    it("accepts the upper bounds (n=100, m=100, |A|=100)") {
      WrightFisherFixationProblem.from(100, 100, Vector.fill(100)(0)) shouldBe a[Right[_, _]]
    }

    it("accepts a recessive count equal to 2n at the boundary") {
      WrightFisherFixationProblem.from(4, 3, Vector(8)) shouldBe a[Right[_, _]]
    }

    it("accepts a recessive count of 0 at the boundary") {
      WrightFisherFixationProblem.from(4, 3, Vector(0)) shouldBe a[Right[_, _]]
    }

    it("rejects non-positive n as NonPositiveN") {
      WrightFisherFixationProblem.from(0, 3, Vector(0)) shouldBe
        Left(WrightFisherFixationProblemError.NonPositiveN(0))
    }

    it("rejects n exceeding 100 as NExceedsMaximum") {
      WrightFisherFixationProblem.from(101, 3, Vector(0)) shouldBe
        Left(WrightFisherFixationProblemError.NExceedsMaximum(101, 100))
    }

    it("rejects non-positive m as NonPositiveM") {
      WrightFisherFixationProblem.from(4, 0, Vector(0)) shouldBe
        Left(WrightFisherFixationProblemError.NonPositiveM(0))
    }

    it("rejects m exceeding 100 as MExceedsMaximum") {
      WrightFisherFixationProblem.from(4, 101, Vector(0)) shouldBe
        Left(WrightFisherFixationProblemError.MExceedsMaximum(101, 100))
    }

    it("rejects recessiveCounts larger than 100 as TooManyRecessiveCounts") {
      WrightFisherFixationProblem.from(4, 3, Vector.fill(101)(0)) shouldBe
        Left(WrightFisherFixationProblemError.TooManyRecessiveCounts(101, 100))
    }

    it("rejects a recessive count below 0 as RecessiveCountOutOfRange") {
      WrightFisherFixationProblem.from(4, 3, Vector(0, -1, 2)) shouldBe
        Left(WrightFisherFixationProblemError.RecessiveCountOutOfRange(1, -1, 8))
    }

    it("rejects a recessive count above 2n as RecessiveCountOutOfRange") {
      WrightFisherFixationProblem.from(4, 3, Vector(0, 9, 2)) shouldBe
        Left(WrightFisherFixationProblemError.RecessiveCountOutOfRange(1, 9, 8))
    }

    it("returns the first offending element in input order when multiple are out-of-range") {
      WrightFisherFixationProblem.from(4, 3, Vector(0, 9, -1, 99)) shouldBe
        Left(WrightFisherFixationProblemError.RecessiveCountOutOfRange(1, 9, 8))
    }

    it("validates n lower bound before any other constraint") {
      WrightFisherFixationProblem.from(0, 0, Vector(-1)) shouldBe
        Left(WrightFisherFixationProblemError.NonPositiveN(0))
    }

    it("validates m upper bound before recessiveCounts checks") {
      WrightFisherFixationProblem.from(4, 101, Vector.fill(101)(-1)) shouldBe
        Left(WrightFisherFixationProblemError.MExceedsMaximum(101, 100))
    }
  }

  describe("WrightFisherFixationProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.genetics.WrightFisherFixationProblem(4, 3, Vector(0, 1, 2))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.genetics.WrightFisherFixationProblem
          |  .from(4, 3, Vector(0, 1, 2)).toOption.get.copy(n = 99)""".stripMargin
      )
    }
  }
}
