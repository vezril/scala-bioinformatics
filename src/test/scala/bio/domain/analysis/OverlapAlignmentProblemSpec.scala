package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OverlapAlignmentProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private val SampleS = "CTAAGGGATTCCGGTAATTAGACAG"
  private val SampleT = "ATAGACCATATGTCAGTGACTGTGTAA"

  describe("OverlapAlignmentProblem.from") {
    it("accepts the canonical Rosalind OAP sample") {
      val result = OverlapAlignmentProblem.from(dna(SampleS), dna(SampleT))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.s.value shouldBe SampleS
      problem.t.value shouldBe SampleT
    }

    it("accepts two empty strings") {
      OverlapAlignmentProblem.from(dna(""), dna("")).isRight shouldBe true
    }

    it("accepts s and t at the 10000-nt upper bound") {
      OverlapAlignmentProblem
        .from(dna("A" * 10000), dna("A" * 10000))
        .isRight shouldBe true
    }

    it("rejects a 10001-nt s as STooLong(10001, 10000)") {
      OverlapAlignmentProblem.from(dna("A" * 10001), dna("A")) shouldBe
        Left(OverlapAlignmentProblemError.STooLong(10001, 10000))
    }

    it("rejects a 10001-nt t as TTooLong(10001, 10000)") {
      OverlapAlignmentProblem.from(dna("A"), dna("A" * 10001)) shouldBe
        Left(OverlapAlignmentProblemError.TTooLong(10001, 10000))
    }

    it("reports STooLong first when both sides exceed their caps (first-failure-wins)") {
      OverlapAlignmentProblem.from(dna("A" * 10001), dna("A" * 10001)) shouldBe
        Left(OverlapAlignmentProblemError.STooLong(10001, 10000))
    }
  }

  describe("OverlapAlignmentProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.OverlapAlignmentProblem(dna("A"), dna("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.OverlapAlignmentProblem
          |  .from(dna("A"), dna("A")).toOption.get.copy(t = dna("C"))""".stripMargin
      )
    }
  }
}
