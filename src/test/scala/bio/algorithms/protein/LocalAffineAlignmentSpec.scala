package bio.algorithms.protein

import bio.domain.protein.{
  LocalAffineAlignment => LocalAffineAlignmentResult,
  LocalAffineAlignmentProblem,
  ProteinString
}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LocalAffineAlignmentSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  private def align(left: String, right: String): LocalAffineAlignmentResult =
    LocalAffineAlignment.compute(
      LocalAffineAlignmentProblem
        .from(protein(left), protein(right))
        .getOrElse(sys.error(s"invalid problem fixture: $left / $right"))
    )

  describe("LocalAffineAlignment.compute") {
    it("reproduces the canonical Rosalind LAFF sample PLEASANTLY/MEANLY") {
      val r = align("PLEASANTLY", "MEANLY")
      r.score shouldBe 12
      r.leftSubstring shouldBe "LEAS"
      r.rightSubstring shouldBe "MEAN"
    }

    it("returns substrings that are contiguous infixes of the inputs") {
      val r = align("PLEASANTLY", "MEANLY")
      "PLEASANTLY".contains(r.leftSubstring) shouldBe true
      "MEANLY".contains(r.rightSubstring) shouldBe true
    }

    it("aligns identical strings MEANLY/MEANLY to themselves with no gaps") {
      val r = align("MEANLY", "MEANLY")
      r.score shouldBe 31
      r.leftSubstring shouldBe "MEANLY"
      r.rightSubstring shouldBe "MEANLY"
    }

    it("scores a single positively-scoring matched pair W/W as 11") {
      val r = align("W", "W")
      r.score shouldBe 11
      r.leftSubstring shouldBe "W"
      r.rightSubstring shouldBe "W"
    }

    it("floors a single negatively-scoring pair A/R at 0 with empty substrings") {
      val r = align("A", "R")
      r.score shouldBe 0
      r.leftSubstring shouldBe ""
      r.rightSubstring shouldBe ""
    }

    it("avoids an unnecessary gap for A/AA (local, scores 4)") {
      val r = align("A", "AA")
      r.score shouldBe 4
      r.leftSubstring shouldBe "A"
      r.rightSubstring shouldBe "A"
    }

    it("returns a zero-score empty result for an empty left") {
      align("", "MEANLY") shouldBe LocalAffineAlignmentResult(0, "", "")
    }

    it("returns a zero-score empty result for an empty right") {
      align("PLEASANTLY", "") shouldBe LocalAffineAlignmentResult(0, "", "")
    }

    it("is symmetric in its score under argument swap") {
      align("PLEASANTLY", "MEANLY").score shouldBe align("MEANLY", "PLEASANTLY").score
    }
  }
}
