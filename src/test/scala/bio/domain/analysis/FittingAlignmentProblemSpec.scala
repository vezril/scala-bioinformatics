package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FittingAlignmentProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private val SampleText =
    "GCAAACCATAAGCCCTACGTGCCGCCTGTTTAAACTCGCGAACTGAATCTTCTGCTTCACGGTGAAAGTACCACAATGGTATCACACCCCAAGGAAAC"
  private val SampleMotif = "GCCGTCAGGCTGGTGTCCG"

  describe("FittingAlignmentProblem.from") {
    it("accepts the canonical Rosalind SIMS sample") {
      val result = FittingAlignmentProblem.from(dna(SampleText), dna(SampleMotif))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.text.value shouldBe SampleText
      problem.motif.value shouldBe SampleMotif
    }

    it("accepts two empty strings") {
      FittingAlignmentProblem.from(dna(""), dna("")).isRight shouldBe true
    }

    it("accepts an empty text with a non-empty motif") {
      FittingAlignmentProblem.from(dna(""), dna("GATTACA")).isRight shouldBe true
    }

    it("accepts a non-empty text with an empty motif") {
      FittingAlignmentProblem.from(dna("GATTACA"), dna("")).isRight shouldBe true
    }

    it("accepts text at the 10000-nt and motif at the 1000-nt upper bounds") {
      FittingAlignmentProblem
        .from(dna("A" * 10000), dna("A" * 1000))
        .isRight shouldBe true
    }

    it("rejects a 10001-nt text as TextTooLong(10001, 10000)") {
      FittingAlignmentProblem.from(dna("A" * 10001), dna("A")) shouldBe
        Left(FittingAlignmentProblemError.TextTooLong(10001, 10000))
    }

    it("rejects a 1001-nt motif as MotifTooLong(1001, 1000)") {
      FittingAlignmentProblem.from(dna("A"), dna("A" * 1001)) shouldBe
        Left(FittingAlignmentProblemError.MotifTooLong(1001, 1000))
    }

    it("reports TextTooLong first when both sides exceed their caps (first-failure-wins)") {
      FittingAlignmentProblem.from(dna("A" * 10001), dna("A" * 1001)) shouldBe
        Left(FittingAlignmentProblemError.TextTooLong(10001, 10000))
    }
  }

  describe("FittingAlignmentProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.FittingAlignmentProblem(dna("A"), dna("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.FittingAlignmentProblem
          |  .from(dna("A"), dna("A")).toOption.get.copy(motif = dna("C"))""".stripMargin
      )
    }
  }
}
