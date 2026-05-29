package bio.algorithms.protein

import bio.domain.protein.{
  LocalAlignment => Alignment,
  LocalAlignmentProblem,
  ProteinString
}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LocalAlignmentSpec extends AnyFunSpec with Matchers {

  private def fixture(left: String, right: String): LocalAlignmentProblem = {
    val l = ProteinString.from(left).getOrElse(sys.error(s"invalid left: $left"))
    val r = ProteinString.from(right).getOrElse(sys.error(s"invalid right: $right"))
    LocalAlignmentProblem
      .from(l, r)
      .getOrElse(sys.error(s"invalid LocalAlignmentProblem fixture: ($left, $right)"))
  }

  describe("LocalAlignment.compute") {
    it("returns the canonical Rosalind LOCA sample output (score 23, LYPRTEINSTRIN / LYEINSTEIN)") {
      val result = LocalAlignment.compute(fixture("MEANLYPRTEINSTRING", "PLEASANTLYEINSTEIN"))
      result shouldBe Alignment(23, "LYPRTEINSTRIN", "LYEINSTEIN")
    }

    it("returns LocalAlignment(0, \"\", \"\") for two empty strings") {
      LocalAlignment.compute(fixture("", "")) shouldBe Alignment(0, "", "")
    }

    it("returns LocalAlignment(0, \"\", \"\") for empty left") {
      LocalAlignment.compute(fixture("", "MEANLY")) shouldBe Alignment(0, "", "")
    }

    it("returns LocalAlignment(0, \"\", \"\") for empty right") {
      LocalAlignment.compute(fixture("PLEASANTLY", "")) shouldBe Alignment(0, "", "")
    }

    it("returns the sum of PAM250 self-substitution values for identical inputs (MEANLY → 30)") {
      // M(6) + E(4) + A(2) + N(2) + L(6) + Y(10) = 30
      val result = LocalAlignment.compute(fixture("MEANLY", "MEANLY"))
      result.score shouldBe 30
      result.leftSubstring shouldBe "MEANLY"
      result.rightSubstring shouldBe "MEANLY"
    }

    it("returns PAM250 diagonal entry for a single-letter self-substitution (W vs W → 17)") {
      val result = LocalAlignment.compute(fixture("W", "W"))
      result.score shouldBe 17
      result.leftSubstring shouldBe "W"
      result.rightSubstring shouldBe "W"
    }

    it("returns score 0 with empty substrings when no positive local alignment exists (A vs R)") {
      // PAM250 A/R = -2 < 0, so the best local alignment is the empty one.
      LocalAlignment.compute(fixture("A", "R")) shouldBe Alignment(0, "", "")
    }

    it("is symmetric in its score (PAM250 is symmetric)") {
      val s1 = LocalAlignment.compute(fixture("MEANLYPRTEINSTRING", "PLEASANTLYEINSTEIN")).score
      val s2 = LocalAlignment.compute(fixture("PLEASANTLYEINSTEIN", "MEANLYPRTEINSTRING")).score
      s1 shouldBe s2
    }
  }
}
