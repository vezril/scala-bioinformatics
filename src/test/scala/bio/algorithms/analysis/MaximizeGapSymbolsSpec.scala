package bio.algorithms.analysis

import bio.domain.analysis.MaxGapProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaximizeGapSymbolsSpec extends AnyFunSpec with Matchers {

  private def gaps(a: String, b: String): Int =
    MaximizeGapSymbols
      .maxGaps(
        MaxGapProblem
          .from(
            DnaString.from(a).getOrElse(sys.error(s"bad DnaString: $a")),
            DnaString.from(b).getOrElse(sys.error(s"bad DnaString: $b"))
          )
          .getOrElse(sys.error("invalid MaxGapProblem fixture"))
      )
      .count

  describe("MaximizeGapSymbols.maxGaps") {
    it("computes the canonical Rosalind MGAP sample") {
      gaps("AACGTA", "ACACCTA") shouldBe 3
    }

    it("needs no gaps for identical strings") {
      gaps("ACGT", "ACGT") shouldBe 0
    }

    it("is all gaps for fully disjoint strings") {
      gaps("AAAA", "CCCC") shouldBe 8
    }

    it("gives a gap per symbol of the other when one string is empty") {
      gaps("ACGT", "") shouldBe 4
    }
  }
}
