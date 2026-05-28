package bio.algorithms.nucleic

import bio.domain.nucleic.{NoncrossingMatchingProblem, RnaString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class NoncrossingMatchingSpec extends AnyFunSpec with Matchers {

  private def fixture(s: String): NoncrossingMatchingProblem = {
    val rna = RnaString.from(s).getOrElse(sys.error(s"invalid RnaString fixture: $s"))
    NoncrossingMatchingProblem
      .from(rna)
      .getOrElse(sys.error(s"invalid NoncrossingMatchingProblem fixture: $s"))
  }

  describe("NoncrossingMatching.count") {
    it("returns 2 for the canonical Rosalind CAT sample `AUAU`") {
      NoncrossingMatching.count(fixture("AUAU")) shouldBe 2
    }

    it("returns 1 for the empty RNA string (the empty matching)") {
      NoncrossingMatching.count(fixture("")) shouldBe 1
    }

    it("returns 1 for a single AU pair") {
      NoncrossingMatching.count(fixture("AU")) shouldBe 1
    }

    it("returns 1 for a single CG pair") {
      NoncrossingMatching.count(fixture("CG")) shouldBe 1
    }

    it("returns 5 for `AUAUAU` (Catalan number C(3))") {
      NoncrossingMatching.count(fixture("AUAUAU")) shouldBe 5
    }

    it("returns 1 for the fully-nested `AAAAUUUU` layout") {
      NoncrossingMatching.count(fixture("AAAAUUUU")) shouldBe 1
    }

    it("returns 1 for the mixed-alphabet `AUCG`") {
      NoncrossingMatching.count(fixture("AUCG")) shouldBe 1
    }

    it("returns 2 for the CG-only `CGCG`") {
      NoncrossingMatching.count(fixture("CGCG")) shouldBe 2
    }

    it("returns 674440 for `\"AU\" * 14` (= C(14) mod 1 000 000)") {
      NoncrossingMatching.count(fixture("AU" * 14)) shouldBe 674440
    }
  }
}
