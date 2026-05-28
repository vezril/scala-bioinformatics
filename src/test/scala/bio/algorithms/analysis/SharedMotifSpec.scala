package bio.algorithms.analysis

import bio.domain.analysis.SharedMotifProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SharedMotifSpec extends AnyFunSpec with Matchers {

  private def fixture(strings: String*): SharedMotifProblem = {
    val sequences = strings.iterator
      .map(s => DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s")))
      .toVector
    SharedMotifProblem
      .from(sequences)
      .getOrElse(sys.error(s"invalid SharedMotifProblem fixture: ${strings.toList}"))
  }

  describe("SharedMotif.find") {
    it("returns \"AC\" for the canonical Rosalind LCSM sample") {
      SharedMotif.find(fixture("GATTACA", "TAGACCA", "ATACA")) shouldBe "AC"
    }

    it("returns the string itself for a single-string collection") {
      SharedMotif.find(fixture("ACGT")) shouldBe "ACGT"
    }

    it("returns the string itself for two identical strings") {
      SharedMotif.find(fixture("ACGT", "ACGT")) shouldBe "ACGT"
    }

    it("returns \"\" when two strings share no character") {
      SharedMotif.find(fixture("AAAA", "CCCC")) shouldBe ""
    }

    it("returns \"\" when the collection contains an empty string") {
      SharedMotif.find(fixture("ACGT", "")) shouldBe ""
    }

    it("returns the lex-smallest single character when no longer LCS exists (`ACGT`, `ATAT` → `A`)") {
      SharedMotif.find(fixture("ACGT", "ATAT")) shouldBe "A"
    }

    it("returns `GTA` for `CCGTAGG`, `AAGTACC`, `TTGTAGT` (unique length-3 LCS)") {
      SharedMotif.find(fixture("CCGTAGG", "AAGTACC", "TTGTAGT")) shouldBe "GTA"
    }
  }
}
