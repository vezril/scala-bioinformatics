package bio.algorithms.nucleic

import bio.domain.nucleic.{PerfectMatchingProblem, RnaString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PerfectMatchingSpec extends AnyFunSpec with Matchers {

  private def fixture(s: String): PerfectMatchingProblem = {
    val rna = RnaString.from(s).getOrElse(sys.error(s"invalid RnaString fixture: $s"))
    PerfectMatchingProblem
      .from(rna)
      .getOrElse(sys.error(s"invalid PerfectMatchingProblem fixture: $s"))
  }

  describe("PerfectMatching.count") {
    it("returns BigInt(12) for the canonical Rosalind PMCH sample `AGCUAGUCAU`") {
      PerfectMatching.count(fixture("AGCUAGUCAU")) shouldBe BigInt(12)
    }

    it("returns BigInt(1) for the empty RNA string (the empty matching)") {
      PerfectMatching.count(fixture("")) shouldBe BigInt(1)
    }

    it("returns BigInt(1) for a single AU pair") {
      PerfectMatching.count(fixture("AU")) shouldBe BigInt(1)
    }

    it("returns BigInt(1) for a single CG pair") {
      PerfectMatching.count(fixture("CG")) shouldBe BigInt(1)
    }

    it("returns BigInt(2) for two AU pairs `AAUU` (= 2!)") {
      PerfectMatching.count(fixture("AAUU")) shouldBe BigInt(2)
    }

    it("returns BigInt(2) for two CG pairs `CCGG` (= 2!)") {
      PerfectMatching.count(fixture("CCGG")) shouldBe BigInt(2)
    }

    it("returns 40! for `AU` repeated 40 times (the 80-char upper boundary)") {
      val expected = BigInt("815915283247897734345611269596115894272000000000")
      PerfectMatching.count(fixture("AU" * 40)) shouldBe expected
    }
  }
}
