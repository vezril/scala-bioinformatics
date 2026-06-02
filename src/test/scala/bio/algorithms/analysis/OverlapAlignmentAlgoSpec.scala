package bio.algorithms.analysis

import bio.domain.analysis.OverlapAlignmentProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OverlapAlignmentAlgoSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private def problem(s: String, t: String): OverlapAlignmentProblem =
    OverlapAlignmentProblem
      .from(dna(s), dna(t))
      .getOrElse(sys.error(s"invalid OverlapAlignmentProblem fixture: $s / $t"))

  /** Recompute the alignment score from the two augmented strings:
    * match +1, substitution -2, gap -2.
    */
  private def scoreOf(aS: String, aT: String): Int =
    aS.zip(aT).foldLeft(0) {
      case (acc, (a, b)) =>
        if (a == '-' || b == '-') acc - 2
        else if (a == b) acc + 1
        else acc - 2
    }

  private val SampleS = "CTAAGGGATTCCGGTAATTAGACAG"
  private val SampleT = "ATAGACCATATGTCAGTGACTGTGTAA"

  describe("OverlapAlignment.align") {
    it("computes the canonical Rosalind OAP sample score of 1") {
      OverlapAlignment.align(problem(SampleS, SampleT)).score shouldBe 1
    }

    it("produces a valid overlap alignment for the canonical sample") {
      val result = OverlapAlignment.align(problem(SampleS, SampleT))
      val aS     = result.augmentedS
      val aT     = result.augmentedT

      // equal lengths
      aS.length shouldBe aT.length
      // no column has a gap in both strings
      aS.zip(aT).exists { case (a, b) => a == '-' && b == '-' } shouldBe false
      // gap-stripped augmentedS is a suffix of s
      val plainS = aS.replace("-", "")
      SampleS.endsWith(plainS) shouldBe true
      // gap-stripped augmentedT is a prefix of t
      val plainT = aT.replace("-", "")
      SampleT.startsWith(plainT) shouldBe true
      // recomputed score matches the reported score
      scoreOf(aS, aT) shouldBe 1
      result.score shouldBe 1
    }

    it("aligns identical strings fully with score equal to the length") {
      val result = OverlapAlignment.align(problem("GATTACA", "GATTACA"))
      result.score shouldBe 7
      result.augmentedS shouldBe "GATTACA"
      result.augmentedT shouldBe "GATTACA"
    }

    it("yields an empty overlap with score 0 for disjoint alphabets") {
      val result = OverlapAlignment.align(problem("AAAA", "TTTT"))
      result.score shouldBe 0
      result.augmentedS shouldBe ""
      result.augmentedT shouldBe ""
    }
  }
}
