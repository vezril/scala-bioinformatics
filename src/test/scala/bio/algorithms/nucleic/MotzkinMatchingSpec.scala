package bio.algorithms.nucleic

import bio.domain.nucleic.{MotzkinMatchingProblem, RnaString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MotzkinMatchingSpec extends AnyFunSpec with Matchers {

  private def fixture(s: String): MotzkinMatchingProblem = {
    val rna = RnaString.from(s).getOrElse(sys.error(s"invalid RnaString fixture: $s"))
    MotzkinMatchingProblem
      .from(rna)
      .getOrElse(sys.error(s"invalid MotzkinMatchingProblem fixture: $s"))
  }

  describe("MotzkinMatching.count") {
    it("returns 7 for the canonical Rosalind MOTZ sample `AUAU`") {
      MotzkinMatching.count(fixture("AUAU")) shouldBe 7
    }

    it("returns 1 for the empty RNA string (the empty matching)") {
      MotzkinMatching.count(fixture("")) shouldBe 1
    }

    it("returns 1 for a single character (only the empty matching)") {
      MotzkinMatching.count(fixture("A")) shouldBe 1
    }

    it("returns 2 for a single AU pair (empty matching + one bond)") {
      MotzkinMatching.count(fixture("AU")) shouldBe 2
    }

    it("returns 1 for the all-A string `AAAA` (no possible bonds)") {
      MotzkinMatching.count(fixture("AAAA")) shouldBe 1
    }

    it("returns 4 for `AAAU` (three single-bond matchings + empty)") {
      MotzkinMatching.count(fixture("AAAU")) shouldBe 4
    }

    it("returns 4 for the mixed-alphabet `AUCG` (4 subsets of two independent pairs)") {
      MotzkinMatching.count(fixture("AUCG")) shouldBe 4
    }

    it("returns 7 for the CG-only `CGCG` (same structure as `AUAU`)") {
      MotzkinMatching.count(fixture("CGCG")) shouldBe 7
    }
  }
}
