package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PerfectMatchingProblemSpec extends AnyFunSpec with Matchers {

  private def rna(s: String): RnaString =
    RnaString.from(s).getOrElse(sys.error(s"invalid RnaString fixture: $s"))

  describe("PerfectMatchingProblem.from") {
    it("accepts the canonical Rosalind PMCH sample with auCount=3 and cgCount=2") {
      val result = PerfectMatchingProblem.from(rna("AGCUAGUCAU"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.auCount shouldBe 3
      problem.cgCount shouldBe 2
    }

    it("accepts the empty RNA string with both counts 0") {
      val result = PerfectMatchingProblem.from(rna(""))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.auCount shouldBe 0
      problem.cgCount shouldBe 0
    }

    it("accepts a maximum-length 80-character input `AU` * 40") {
      val result = PerfectMatchingProblem.from(rna("AU" * 40))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.rna.value.length shouldBe 80
      problem.auCount shouldBe 40
      problem.cgCount shouldBe 0
    }

    it("rejects an 81-character input as ExceedsMaxLength(81, 80)") {
      // 40 A's + 41 U's = 81 chars, balanced for C/G (both zero) but oversized.
      val oversized = "A" * 40 + "U" * 41
      PerfectMatchingProblem.from(rna(oversized)) shouldBe
        Left(PerfectMatchingProblemError.ExceedsMaxLength(81, 80))
    }

    it("rejects an unbalanced AU input `AAU` as UnpairedAU(2, 1)") {
      PerfectMatchingProblem.from(rna("AAU")) shouldBe
        Left(PerfectMatchingProblemError.UnpairedAU(2, 1))
    }

    it("rejects an unbalanced CG input `CCG` as UnpairedCG(2, 1)") {
      PerfectMatchingProblem.from(rna("CCG")) shouldBe
        Left(PerfectMatchingProblemError.UnpairedCG(2, 1))
    }
  }

  describe("PerfectMatchingProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.nucleic.PerfectMatchingProblem(rna("AU"), 1, 0)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.nucleic.PerfectMatchingProblem
          |  .from(rna("AU")).toOption.get.copy(auCount = 99)""".stripMargin
      )
    }
  }
}
