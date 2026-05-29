package bio.algorithms.protein

import bio.domain.protein.{
  AffineGapAlignment => AffineGapAlignmentResult,
  AffineGapAlignmentProblem,
  AminoAcid,
  ProteinString
}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AffineGapAlignmentSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  private def align(left: String, right: String): AffineGapAlignmentResult =
    AffineGapAlignment.compute(
      AffineGapAlignmentProblem
        .from(protein(left), protein(right))
        .getOrElse(sys.error(s"invalid problem fixture: $left / $right"))
    )

  private val aaOf: Map[Char, AminoAcid] =
    AminoAcid.all.map(aa => aa.code -> aa).toMap

  /** Independent re-scorer: affine score of an augmented alignment under
    * BLOSUM62 with gap-open 11 and gap-extend 1. A maximal run of contiguous
    * gaps in one row costs `11 + (L - 1)`; switching gap side starts a new gap.
    */
  private def affineScore(augLeft: String, augRight: String): Int = {
    val a        = 11
    val b        = 1
    var total    = 0
    var prevSide = 0 // 0 none/match, 1 gap-in-left, 2 gap-in-right
    var k        = 0
    while (k < augLeft.length) {
      val l = augLeft.charAt(k)
      val r = augRight.charAt(k)
      if (l == '-') {
        total -= (if (prevSide == 1) b else a)
        prevSide = 1
      } else if (r == '-') {
        total -= (if (prevSide == 2) b else a)
        prevSide = 2
      } else {
        total += Blosum62.score(aaOf(l), aaOf(r))
        prevSide = 0
      }
      k += 1
    }
    total
  }

  describe("AffineGapAlignment.compute") {
    it("scores the canonical Rosalind GAFF sample PRTEINS/PRTWPSEIN as 8") {
      align("PRTEINS", "PRTWPSEIN").score shouldBe 8
    }

    it("reproduces the canonical published alignment PRT---EINS / PRTWPSEIN-") {
      val r = align("PRTEINS", "PRTWPSEIN")
      r.augmentedLeft shouldBe "PRT---EINS"
      r.augmentedRight shouldBe "PRTWPSEIN-"
    }

    it("returns a structurally valid alignment for the canonical sample") {
      val r = align("PRTEINS", "PRTWPSEIN")
      r.augmentedLeft.length shouldBe r.augmentedRight.length
      r.augmentedLeft
        .zip(r.augmentedRight)
        .exists { case (l, rr) => l == '-' && rr == '-' } shouldBe false
      r.augmentedLeft.filterNot(_ == '-') shouldBe "PRTEINS"
      r.augmentedRight.filterNot(_ == '-') shouldBe "PRTWPSEIN"
      affineScore(r.augmentedLeft, r.augmentedRight) shouldBe r.score
    }

    it("scores identical PRTEINS/PRTEINS as 36 with no gaps") {
      val r = align("PRTEINS", "PRTEINS")
      r.score shouldBe 36
      r.augmentedLeft shouldBe "PRTEINS"
      r.augmentedRight shouldBe "PRTEINS"
    }

    it("scores an empty left against MEANLY as a single affine gap (-16)") {
      val r = align("", "MEANLY")
      r.score shouldBe -16
      r.augmentedLeft shouldBe "------"
      r.augmentedRight shouldBe "MEANLY"
    }

    it("scores PRTEINS against an empty right as a single affine gap (-17)") {
      val r = align("PRTEINS", "")
      r.score shouldBe -17
      r.augmentedLeft shouldBe "PRTEINS"
      r.augmentedRight shouldBe "-------"
    }

    it("scores two empty strings as 0 with empty augmented strings") {
      val r = align("", "")
      r.score shouldBe 0
      r.augmentedLeft shouldBe ""
      r.augmentedRight shouldBe ""
    }

    it("scores a single matched pair W/W as 11") {
      val r = align("W", "W")
      r.score shouldBe 11
      r.augmentedLeft shouldBe "W"
      r.augmentedRight shouldBe "W"
    }

    it("scores a single mismatched pair A/R as -1 with no gaps") {
      val r = align("A", "R")
      r.score shouldBe -1
      r.augmentedLeft shouldBe "A"
      r.augmentedRight shouldBe "R"
    }

    it("charges the gap penalty proportionally to length (affine, not constant)") {
      align("A", "AA").score shouldBe -7
      align("A", "AAA").score shouldBe -8
    }

    it("is symmetric in its score under argument swap") {
      align("PRTEINS", "PRTWPSEIN").score shouldBe align("PRTWPSEIN", "PRTEINS").score
    }
  }
}
