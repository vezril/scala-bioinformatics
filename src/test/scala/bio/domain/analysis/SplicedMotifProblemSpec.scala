package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SplicedMotifProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("SplicedMotifProblem.from") {
    it("accepts the canonical Rosalind SSEQ sample") {
      val result = SplicedMotifProblem.from(dna("ACGTACGTGACG"), dna("GTA"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.source.value shouldBe "ACGTACGTGACG"
      problem.target.value shouldBe "GTA"
    }

    it("accepts an empty source and empty target") {
      val result = SplicedMotifProblem.from(dna(""), dna(""))
      result.isRight shouldBe true
      result.toOption.get.source.value shouldBe ""
      result.toOption.get.target.value shouldBe ""
    }

    it("accepts a non-empty source with empty target") {
      val result = SplicedMotifProblem.from(dna("ACGT"), dna(""))
      result.isRight shouldBe true
    }

    it("accepts both strings at the 1000-character upper bound") {
      val result = SplicedMotifProblem.from(dna("A" * 1000), dna("A" * 1000))
      result.isRight shouldBe true
    }

    it("rejects a 1001-character source as SourceTooLong(1001, 1000)") {
      SplicedMotifProblem.from(dna("A" * 1001), dna("A")) shouldBe
        Left(SplicedMotifProblemError.SourceTooLong(1001, 1000))
    }

    it("rejects a 1001-character target as TargetTooLong(1001, 1000)") {
      SplicedMotifProblem.from(dna("A"), dna("A" * 1001)) shouldBe
        Left(SplicedMotifProblemError.TargetTooLong(1001, 1000))
    }
  }

  describe("SplicedMotifProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.SplicedMotifProblem(dna("A"), dna("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.SplicedMotifProblem
          |  .from(dna("ACGT"), dna("A")).toOption.get.copy(target = dna("G"))""".stripMargin
      )
    }
  }
}
