package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class NoncrossingMatchingProblemSpec extends AnyFunSpec with Matchers {

  private def rna(s: String): RnaString =
    RnaString.from(s).getOrElse(sys.error(s"invalid RnaString fixture: $s"))

  describe("NoncrossingMatchingProblem.from") {
    it("accepts the canonical Rosalind CAT sample `AUAU` (auCount=2, cgCount=0)") {
      val result = NoncrossingMatchingProblem.from(rna("AUAU"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.auCount shouldBe 2
      problem.cgCount shouldBe 0
    }

    it("accepts the empty RNA string with both counts 0") {
      val result = NoncrossingMatchingProblem.from(rna(""))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.auCount shouldBe 0
      problem.cgCount shouldBe 0
    }

    it("accepts the maximum 300-character input `AU` * 150") {
      val result = NoncrossingMatchingProblem.from(rna("AU" * 150))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.rna.value.length shouldBe 300
      problem.auCount shouldBe 150
      problem.cgCount shouldBe 0
    }

    it("rejects a 301-character input as ExceedsMaxLength(301, 300)") {
      // 151 A's + 150 U's = 301 chars, balanced for C/G (both zero) but oversized.
      val oversized = "A" * 151 + "U" * 150
      NoncrossingMatchingProblem.from(rna(oversized)) shouldBe
        Left(NoncrossingMatchingProblemError.ExceedsMaxLength(301, 300))
    }

    it("rejects an unbalanced AU input `AAU` as UnpairedAU(2, 1)") {
      NoncrossingMatchingProblem.from(rna("AAU")) shouldBe
        Left(NoncrossingMatchingProblemError.UnpairedAU(2, 1))
    }

    it("rejects an unbalanced CG input `CCG` as UnpairedCG(2, 1)") {
      NoncrossingMatchingProblem.from(rna("CCG")) shouldBe
        Left(NoncrossingMatchingProblemError.UnpairedCG(2, 1))
    }
  }

  describe("NoncrossingMatchingProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.nucleic.NoncrossingMatchingProblem(rna("AU"), 1, 0)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.nucleic.NoncrossingMatchingProblem
          |  .from(rna("AU")).toOption.get.copy(auCount = 99)""".stripMargin
      )
    }
  }
}
