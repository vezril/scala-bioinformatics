package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SemiglobalAlignmentProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private val SampleS = "CAGCACTTGGATTCTCGG"
  private val SampleT = "CAGCGTGG"

  describe("SemiglobalAlignmentProblem.from") {
    it("accepts the canonical Rosalind SMGB sample") {
      val result = SemiglobalAlignmentProblem.from(dna(SampleS), dna(SampleT))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.s.value shouldBe SampleS
      problem.t.value shouldBe SampleT
    }

    it("accepts two empty strings") {
      SemiglobalAlignmentProblem.from(dna(""), dna("")).isRight shouldBe true
    }

    it("rejects a 10001-nt s as STooLong(10001, 10000)") {
      SemiglobalAlignmentProblem.from(dna("A" * 10001), dna("A")) shouldBe
        Left(SemiglobalAlignmentProblemError.STooLong(10001, 10000))
    }

    it("rejects a 10001-nt t as TTooLong(10001, 10000)") {
      SemiglobalAlignmentProblem.from(dna("A"), dna("A" * 10001)) shouldBe
        Left(SemiglobalAlignmentProblemError.TTooLong(10001, 10000))
    }

    it("reports STooLong first when both sides exceed their caps (first-failure-wins)") {
      SemiglobalAlignmentProblem.from(dna("A" * 10001), dna("A" * 10001)) shouldBe
        Left(SemiglobalAlignmentProblemError.STooLong(10001, 10000))
    }
  }

  describe("SemiglobalAlignmentProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.SemiglobalAlignmentProblem(dna("A"), dna("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.SemiglobalAlignmentProblem
          |  .from(dna("A"), dna("A")).toOption.get.copy(t = dna("C"))""".stripMargin
      )
    }
  }
}
