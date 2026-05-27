package bio.algorithms.combinatorics

import bio.domain.combinatorics.PartialPermutationProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PartialPermutationsSpec extends AnyFunSpec with Matchers {

  private def pp(n: Int, k: Int): PartialPermutationProblem =
    PartialPermutationProblem
      .from(n, k)
      .getOrElse(sys.error(s"invalid PartialPermutationProblem in fixture: ($n, $k)"))

  describe("PartialPermutations.count") {
    it("produces 51200 for the Rosalind sample (n=21, k=7)") {
      PartialPermutations.count(pp(21, 7)) shouldBe 51200
    }

    it("produces n for any (n, 1) — P(n, 1) = n") {
      PartialPermutations.count(pp(5, 1)) shouldBe 5
    }

    it("produces 1 for P(1, 1)") {
      PartialPermutations.count(pp(1, 1)) shouldBe 1
    }

    it("produces 120 for P(5, 5) = 5!") {
      PartialPermutations.count(pp(5, 5)) shouldBe 120
    }

    it("produces 5040 for P(7, 7) = 7!") {
      PartialPermutations.count(pp(7, 7)) shouldBe 5040
    }

    it("produces 720 for P(10, 3) = 10 × 9 × 8") {
      PartialPermutations.count(pp(10, 3)) shouldBe 720
    }

    it("produces 472000 for the upper-bound (n=100, k=10) — P(100, 10) mod 1,000,000") {
      PartialPermutations.count(pp(100, 10)) shouldBe 472000
    }
  }
}
