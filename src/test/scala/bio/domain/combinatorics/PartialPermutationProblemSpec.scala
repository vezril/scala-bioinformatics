package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PartialPermutationProblemSpec extends AnyFunSpec with Matchers {

  describe("PartialPermutationProblem.from") {
    it("accepts the Rosalind sample (21, 7) and exposes both fields") {
      val problem = PartialPermutationProblem.from(21, 7).toOption.get
      problem.n shouldBe 21
      problem.k shouldBe 7
    }

    it("accepts the minimum-bound parameters (1, 1)") {
      val problem = PartialPermutationProblem.from(1, 1).toOption.get
      problem.n shouldBe 1
      problem.k shouldBe 1
    }

    it("accepts the upper-bound parameters (100, 10)") {
      val problem = PartialPermutationProblem.from(100, 10).toOption.get
      problem.n shouldBe 100
      problem.k shouldBe 10
    }

    it("accepts the equal-bound case n = k (5, 5)") {
      val problem = PartialPermutationProblem.from(5, 5).toOption.get
      problem.n shouldBe 5
      problem.k shouldBe 5
    }

    it("rejects zero n") {
      PartialPermutationProblem.from(0, 1) shouldBe
        Left(PartialPermutationProblemError.NonPositiveN(0))
    }

    it("rejects n exceeding 100") {
      PartialPermutationProblem.from(101, 1) shouldBe
        Left(PartialPermutationProblemError.NExceedsMaximum(101, 100))
    }

    it("rejects zero k") {
      PartialPermutationProblem.from(10, 0) shouldBe
        Left(PartialPermutationProblemError.NonPositiveK(0))
    }

    it("rejects k exceeding 10") {
      PartialPermutationProblem.from(10, 11) shouldBe
        Left(PartialPermutationProblemError.KExceedsMaximum(11, 10))
    }

    it("rejects k > n as KExceedsN") {
      PartialPermutationProblem.from(3, 5) shouldBe
        Left(PartialPermutationProblemError.KExceedsN(5, 3))
    }

    it("validates n lower bound before all other constraints") {
      PartialPermutationProblem.from(0, 0) shouldBe
        Left(PartialPermutationProblemError.NonPositiveN(0))
    }

    it("validates n upper bound before k checks") {
      PartialPermutationProblem.from(101, 0) shouldBe
        Left(PartialPermutationProblemError.NExceedsMaximum(101, 100))
    }
  }

  describe("PartialPermutationProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.combinatorics.PartialPermutationProblem(21, 7)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.PartialPermutationProblem.from(21, 7).toOption.get.copy(n = 99)"""
      )
    }
  }
}
