package bio.algorithms.analysis

import bio.domain.analysis.SemiglobalAlignmentProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SemiglobalAlignmentAlgoSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private def problem(s: String, t: String): SemiglobalAlignmentProblem =
    SemiglobalAlignmentProblem
      .from(dna(s), dna(t))
      .getOrElse(sys.error(s"invalid SemiglobalAlignmentProblem fixture: $s / $t"))

  /** Recompute the semiglobal score from the two augmented strings: match +1,
    * substitution -1, internal gap -1, with leading/trailing gap runs free.
    */
  private def scoreOf(aS: String, aT: String): Int = {
    val n = aS.length

    // Leading free region: columns where one side is still in its leading gap run.
    val leadingS = aS.takeWhile(_ == '-').length
    val leadingT = aT.takeWhile(_ == '-').length
    val leadFree = math.max(leadingS, leadingT)

    // Trailing free region: columns where one side is in its trailing gap run.
    val trailingS = aS.reverse.takeWhile(_ == '-').length
    val trailingT = aT.reverse.takeWhile(_ == '-').length
    val trailFree = math.max(trailingS, trailingT)

    var score = 0
    var i     = leadFree
    while (i < n - trailFree) {
      val a = aS.charAt(i)
      val b = aT.charAt(i)
      if (a == '-' || b == '-') score -= 1
      else if (a == b) score += 1
      else score -= 1
      i += 1
    }
    score
  }

  private val SampleS = "CAGCACTTGGATTCTCGG"
  private val SampleT = "CAGCGTGG"

  describe("SemiglobalAlignment.align") {
    it("computes the canonical Rosalind SMGB sample score of 4") {
      SemiglobalAlignment.align(problem(SampleS, SampleT)).score shouldBe 4
    }

    it("produces a valid semiglobal alignment for the canonical sample") {
      val result = SemiglobalAlignment.align(problem(SampleS, SampleT))
      val aS     = result.augmentedS
      val aT     = result.augmentedT

      // equal lengths
      aS.length shouldBe aT.length
      // no column has a gap in both strings
      aS.zip(aT).exists { case (a, b) => a == '-' && b == '-' } shouldBe false
      // gap-stripped augmentedS reproduces s
      aS.replace("-", "") shouldBe SampleS
      // gap-stripped augmentedT reproduces t
      aT.replace("-", "") shouldBe SampleT
      // recomputed free-end-gap score matches the reported score
      scoreOf(aS, aT) shouldBe 4
      result.score shouldBe 4
    }

    it("aligns identical strings fully with score equal to the length") {
      val result = SemiglobalAlignment.align(problem("GATTACA", "GATTACA"))
      result.score shouldBe 7
      result.augmentedS shouldBe "GATTACA"
      result.augmentedT shouldBe "GATTACA"
    }

    it("aligns a contained string with free end gaps") {
      val result = SemiglobalAlignment.align(problem("ACGTACGT", "GTAC"))
      result.score shouldBe 4
      result.augmentedS.replace("-", "") shouldBe "ACGTACGT"
      result.augmentedT.replace("-", "") shouldBe "GTAC"
    }
  }
}
