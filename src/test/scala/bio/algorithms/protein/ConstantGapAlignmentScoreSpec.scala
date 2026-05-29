package bio.algorithms.protein

import bio.domain.protein.{ConstantGapAlignmentScoreProblem, ProteinString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ConstantGapAlignmentScoreSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  private def score(left: String, right: String): Int =
    ConstantGapAlignmentScore.compute(
      ConstantGapAlignmentScoreProblem
        .from(protein(left), protein(right))
        .getOrElse(sys.error(s"invalid problem fixture: $left / $right"))
    )

  describe("ConstantGapAlignmentScore.compute") {
    it("scores the canonical Rosalind GCON sample PLEASANTLY/MEANLY as 13") {
      score("PLEASANTLY", "MEANLY") shouldBe 13
    }

    it("scores identical MEANLY/MEANLY as the sum of self-substitution values (31)") {
      score("MEANLY", "MEANLY") shouldBe 31
    }

    it("scores an empty left against MEANLY as a single constant gap (-5)") {
      score("", "MEANLY") shouldBe -5
    }

    it("scores PLEASANTLY against an empty right as a single constant gap (-5)") {
      score("PLEASANTLY", "") shouldBe -5
    }

    it("scores two empty strings as 0") {
      score("", "") shouldBe 0
    }

    it("scores a single matched pair W/W as its BLOSUM62 diagonal value (11)") {
      score("W", "W") shouldBe 11
    }

    it("scores a single mismatched pair A/R as its BLOSUM62 off-diagonal value (-1)") {
      score("A", "R") shouldBe -1
    }

    it("charges the gap penalty independent of gap length (A/AA and A/ten-As both -1)") {
      score("A", "AA") shouldBe -1
      score("A", "A" * 10) shouldBe -1
    }

    it("is symmetric in its arguments") {
      score("PLEASANTLY", "MEANLY") shouldBe score("MEANLY", "PLEASANTLY")
    }
  }
}
