package bio.algorithms.protein

import bio.domain.protein.{GlobalAlignmentScoreProblem, ProteinString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GlobalAlignmentScoreSpec extends AnyFunSpec with Matchers {

  private def fixture(left: String, right: String): GlobalAlignmentScoreProblem = {
    val l = ProteinString.from(left).getOrElse(sys.error(s"invalid left: $left"))
    val r = ProteinString.from(right).getOrElse(sys.error(s"invalid right: $right"))
    GlobalAlignmentScoreProblem
      .from(l, r)
      .getOrElse(sys.error(s"invalid GlobalAlignmentScoreProblem fixture: ($left, $right)"))
  }

  describe("GlobalAlignmentScore.compute") {
    it("returns 8 for the canonical Rosalind GLOB sample PLEASANTLY / MEANLY") {
      GlobalAlignmentScore.compute(fixture("PLEASANTLY", "MEANLY")) shouldBe 8
    }

    it("returns the sum of self-substitution diagonal values for identical inputs (MEANLY → 31)") {
      // M/M(5) + E/E(5) + A/A(4) + N/N(6) + L/L(4) + Y/Y(7) = 31
      GlobalAlignmentScore.compute(fixture("MEANLY", "MEANLY")) shouldBe 31
    }

    it("returns -5 × length(right) when left is empty") {
      GlobalAlignmentScore.compute(fixture("", "MEANLY")) shouldBe -30
    }

    it("returns -5 × length(left) when right is empty") {
      GlobalAlignmentScore.compute(fixture("PLEASANTLY", "")) shouldBe -50
    }

    it("returns 0 when both strings are empty") {
      GlobalAlignmentScore.compute(fixture("", "")) shouldBe 0
    }

    it("returns the BLOSUM62 diagonal entry for a single-letter self-substitution (W vs W → 11)") {
      GlobalAlignmentScore.compute(fixture("W", "W")) shouldBe 11
    }

    it("returns the BLOSUM62 off-diagonal entry for a single-letter cross-substitution (A vs R → -1)") {
      GlobalAlignmentScore.compute(fixture("A", "R")) shouldBe -1
    }

    it("returns -1 for A vs AC (best is A/A match + one trailing gap on left)") {
      GlobalAlignmentScore.compute(fixture("A", "AC")) shouldBe -1
    }

    it("is symmetric in its arguments (BLOSUM62 is symmetric)") {
      val s1 = GlobalAlignmentScore.compute(fixture("PLEASANTLY", "MEANLY"))
      val s2 = GlobalAlignmentScore.compute(fixture("MEANLY", "PLEASANTLY"))
      s1 shouldBe s2
    }
  }
}
