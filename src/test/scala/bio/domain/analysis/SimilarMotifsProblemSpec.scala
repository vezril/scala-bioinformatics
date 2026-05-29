package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SimilarMotifsProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("SimilarMotifsProblem.from") {
    it("accepts the canonical Rosalind KSIM sample") {
      val result =
        SimilarMotifsProblem.from(2, dna("ACGTAG"), dna("ACGGATCGGCATCGT"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.k shouldBe 2
      problem.motif.value shouldBe "ACGTAG"
      problem.genome.value shouldBe "ACGGATCGGCATCGT"
    }

    it("accepts k at the lower bound (1)") {
      SimilarMotifsProblem.from(1, dna("ACG"), dna("ACG")).isRight shouldBe true
    }

    it("accepts k at the upper bound (50)") {
      SimilarMotifsProblem.from(50, dna("ACG"), dna("ACG")).isRight shouldBe true
    }

    it("accepts an empty motif and an empty genome") {
      SimilarMotifsProblem.from(1, dna(""), dna("")).isRight shouldBe true
    }

    it("accepts motif and genome at their upper bounds") {
      SimilarMotifsProblem
        .from(1, dna("A" * 5000), dna("A" * 50000))
        .isRight shouldBe true
    }

    it("rejects k = 0 as KOutOfRange(0, 1, 50)") {
      SimilarMotifsProblem.from(0, dna("ACG"), dna("ACG")) shouldBe
        Left(SimilarMotifsProblemError.KOutOfRange(0, 1, 50))
    }

    it("rejects k = 51 as KOutOfRange(51, 1, 50)") {
      SimilarMotifsProblem.from(51, dna("ACG"), dna("ACG")) shouldBe
        Left(SimilarMotifsProblemError.KOutOfRange(51, 1, 50))
    }

    it("rejects a 5001-bp motif as MotifTooLong(5001, 5000)") {
      SimilarMotifsProblem.from(1, dna("A" * 5001), dna("ACG")) shouldBe
        Left(SimilarMotifsProblemError.MotifTooLong(5001, 5000))
    }

    it("rejects a 50001-bp genome as GenomeTooLong(50001, 50000)") {
      SimilarMotifsProblem.from(1, dna("ACG"), dna("A" * 50001)) shouldBe
        Left(SimilarMotifsProblemError.GenomeTooLong(50001, 50000))
    }

    it("reports KOutOfRange first when k is invalid and the motif is too long (first-failure-wins)") {
      SimilarMotifsProblem.from(0, dna("A" * 5001), dna("ACG")) shouldBe
        Left(SimilarMotifsProblemError.KOutOfRange(0, 1, 50))
    }
  }

  describe("SimilarMotifsProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.SimilarMotifsProblem(2, dna("ACG"), dna("ACG"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.SimilarMotifsProblem
          |  .from(2, dna("ACG"), dna("ACG")).toOption.get.copy(k = 3)""".stripMargin
      )
    }
  }
}
