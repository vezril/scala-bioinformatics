package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CombinationSumProblemSpec extends AnyFunSpec with Matchers {

  describe("CombinationSumProblem.from") {
    it("accepts the Rosalind sample (n=6, m=3)") {
      val problem = CombinationSumProblem.from(6, 3).toOption.get
      problem.n shouldBe 6
      problem.m shouldBe 3
    }

    it("accepts the minimum bounds (n=0, m=0)") {
      val problem = CombinationSumProblem.from(0, 0).toOption.get
      problem.n shouldBe 0
      problem.m shouldBe 0
    }

    it("accepts the upper bounds (n=2000, m=2000)") {
      val problem = CombinationSumProblem.from(2000, 2000).toOption.get
      problem.n shouldBe 2000
      problem.m shouldBe 2000
    }

    it("accepts equal m and n at a mid-range value (5, 5)") {
      val problem = CombinationSumProblem.from(5, 5).toOption.get
      problem.n shouldBe 5
      problem.m shouldBe 5
    }

    it("rejects negative n as NegativeN") {
      CombinationSumProblem.from(-1, 0) shouldBe
        Left(CombinationSumProblemError.NegativeN(-1))
    }

    it("rejects n exceeding 2000 as NExceedsMaximum") {
      CombinationSumProblem.from(2001, 0) shouldBe
        Left(CombinationSumProblemError.NExceedsMaximum(2001, 2000))
    }

    it("rejects negative m as NegativeM") {
      CombinationSumProblem.from(10, -1) shouldBe
        Left(CombinationSumProblemError.NegativeM(-1))
    }

    it("rejects m > n as MExceedsN") {
      CombinationSumProblem.from(3, 5) shouldBe
        Left(CombinationSumProblemError.MExceedsN(5, 3))
    }

    it("validates n lower bound before any other constraint") {
      CombinationSumProblem.from(-1, -1) shouldBe
        Left(CombinationSumProblemError.NegativeN(-1))
    }

    it("validates n upper bound before m checks") {
      CombinationSumProblem.from(2001, -1) shouldBe
        Left(CombinationSumProblemError.NExceedsMaximum(2001, 2000))
    }

    it("validates m lower bound before the m <= n cross-constraint") {
      // -5 <= 3 is true, so cross-constraint alone wouldn't trigger; the m<0 check must win
      CombinationSumProblem.from(3, -5) shouldBe
        Left(CombinationSumProblemError.NegativeM(-5))
    }
  }

  describe("CombinationSumProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.combinatorics.CombinationSumProblem(6, 3)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.CombinationSumProblem
          |  .from(6, 3).toOption.get.copy(n = 99)""".stripMargin
      )
    }
  }
}
