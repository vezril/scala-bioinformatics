package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MotzkinMatchingProblemSpec extends AnyFunSpec with Matchers {

  private def rna(s: String): RnaString =
    RnaString.from(s).getOrElse(sys.error(s"invalid RnaString fixture: $s"))

  describe("MotzkinMatchingProblem.from") {
    it("accepts the canonical Rosalind MOTZ sample `AUAU`") {
      val result = MotzkinMatchingProblem.from(rna("AUAU"))
      result.isRight shouldBe true
      result.toOption.get.rna.value shouldBe "AUAU"
    }

    it("accepts the empty RNA string") {
      val result = MotzkinMatchingProblem.from(rna(""))
      result.isRight shouldBe true
      result.toOption.get.rna.value shouldBe ""
    }

    it("accepts the maximum-length 300-character input") {
      val result = MotzkinMatchingProblem.from(rna("A" * 300))
      result.isRight shouldBe true
      result.toOption.get.rna.value.length shouldBe 300
    }

    it("accepts a single-character input `A`") {
      val result = MotzkinMatchingProblem.from(rna("A"))
      result.isRight shouldBe true
      result.toOption.get.rna.value shouldBe "A"
    }

    it("accepts an unbalanced AU string `AAU` (which CAT would reject)") {
      val result = MotzkinMatchingProblem.from(rna("AAU"))
      result.isRight shouldBe true
      result.toOption.get.rna.value shouldBe "AAU"
    }

    it("accepts an odd-length string `AUC`") {
      val result = MotzkinMatchingProblem.from(rna("AUC"))
      result.isRight shouldBe true
      result.toOption.get.rna.value shouldBe "AUC"
    }

    it("rejects a 301-character input as ExceedsMaxLength(301, 300)") {
      MotzkinMatchingProblem.from(rna("A" * 301)) shouldBe
        Left(MotzkinMatchingProblemError.ExceedsMaxLength(301, 300))
    }
  }

  describe("MotzkinMatchingProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.nucleic.MotzkinMatchingProblem(rna("AU"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.nucleic.MotzkinMatchingProblem
          |  .from(rna("AU")).toOption.get.copy(rna = rna("CG"))""".stripMargin
      )
    }
  }
}
