package bio.algorithms.combinatorics

import bio.domain.combinatorics.CombinationSumProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CombinationsSpec extends AnyFunSpec with Matchers {

  private def problem(n: Int, m: Int): CombinationSumProblem =
    CombinationSumProblem
      .from(n, m)
      .getOrElse(sys.error(s"invalid CombinationSumProblem fixture: ($n, $m)"))

  describe("Combinations.sumFrom") {
    it("produces 42 for the Rosalind sample (n=6, m=3)") {
      Combinations.sumFrom(problem(6, 3)) shouldBe 42
    }

    it("produces 1 for n=6, m=6 (just C(6,6))") {
      Combinations.sumFrom(problem(6, 6)) shouldBe 1
    }

    it("produces 64 for n=6, m=0 (full row sum = 2^6)") {
      Combinations.sumFrom(problem(6, 0)) shouldBe 64
    }

    it("produces 1 for n=0, m=0 (just C(0,0))") {
      Combinations.sumFrom(problem(0, 0)) shouldBe 1
    }

    it("produces 638 for the mid-range example (n=10, m=5)") {
      Combinations.sumFrom(problem(10, 5)) shouldBe 638
    }

    it("produces 29376 for the upper bound (n=2000, m=0) — = 2^2000 mod 1_000_000") {
      Combinations.sumFrom(problem(2000, 0)) shouldBe 29376
    }

    it("produces 1 for the upper bound (n=2000, m=2000)") {
      Combinations.sumFrom(problem(2000, 2000)) shouldBe 1
    }
  }
}
