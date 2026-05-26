package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class IndependentAllelesProblemSpec extends AnyFunSpec with Matchers {

  describe("IndependentAllelesProblem.from") {
    it("accepts the Rosalind sample (2, 1) and exposes generations, atLeast, populationSize") {
      val problem = IndependentAllelesProblem.from(2, 1).toOption.get
      problem.generations    shouldBe 2
      problem.atLeast        shouldBe 1
      problem.populationSize shouldBe 4L
    }

    it("accepts the boundary case where atLeast equals 2^generations") {
      val problem = IndependentAllelesProblem.from(3, 8).toOption.get
      problem.generations    shouldBe 3
      problem.atLeast        shouldBe 8
      problem.populationSize shouldBe 8L
    }

    it("rejects zero generations") {
      IndependentAllelesProblem.from(0, 1) shouldBe
        Left(IndependentAllelesProblemError.NonPositiveGenerations(0))
    }

    it("rejects negative generations") {
      IndependentAllelesProblem.from(-1, 1) shouldBe
        Left(IndependentAllelesProblemError.NonPositiveGenerations(-1))
    }

    it("rejects zero atLeast") {
      IndependentAllelesProblem.from(2, 0) shouldBe
        Left(IndependentAllelesProblemError.NonPositiveAtLeast(0))
    }

    it("rejects negative atLeast") {
      IndependentAllelesProblem.from(2, -3) shouldBe
        Left(IndependentAllelesProblemError.NonPositiveAtLeast(-3))
    }

    it("rejects atLeast exceeding 2^generations") {
      IndependentAllelesProblem.from(2, 5) shouldBe
        Left(IndependentAllelesProblemError.AtLeastExceedsPopulation(5, 2))
    }

    it("validates generations before atLeast when both are invalid") {
      IndependentAllelesProblem.from(0, 0) shouldBe
        Left(IndependentAllelesProblemError.NonPositiveGenerations(0))
    }
  }

  describe("IndependentAllelesProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.genetics.IndependentAllelesProblem(2, 1)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.genetics.IndependentAllelesProblem.from(2, 1).toOption.get.copy(generations = 99)"""
      )
    }
  }
}
