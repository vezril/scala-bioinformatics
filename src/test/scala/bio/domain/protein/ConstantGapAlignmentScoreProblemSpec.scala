package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ConstantGapAlignmentScoreProblemSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  describe("ConstantGapAlignmentScoreProblem.from") {
    it("accepts the canonical Rosalind GCON sample") {
      val result =
        ConstantGapAlignmentScoreProblem.from(protein("PLEASANTLY"), protein("MEANLY"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.left.value shouldBe "PLEASANTLY"
      problem.right.value shouldBe "MEANLY"
    }

    it("accepts two empty strings") {
      ConstantGapAlignmentScoreProblem.from(protein(""), protein("")).isRight shouldBe true
    }

    it("accepts an empty left with a non-empty right") {
      ConstantGapAlignmentScoreProblem.from(protein(""), protein("MEANLY")).isRight shouldBe true
    }

    it("accepts a non-empty left with an empty right") {
      ConstantGapAlignmentScoreProblem.from(protein("PLEASANTLY"), protein("")).isRight shouldBe true
    }

    it("accepts both strings at the 1000-aa upper bound") {
      ConstantGapAlignmentScoreProblem
        .from(protein("A" * 1000), protein("A" * 1000))
        .isRight shouldBe true
    }

    it("rejects a 1001-aa left as LeftTooLong(1001, 1000)") {
      ConstantGapAlignmentScoreProblem.from(protein("A" * 1001), protein("A")) shouldBe
        Left(ConstantGapAlignmentScoreProblemError.LeftTooLong(1001, 1000))
    }

    it("rejects a 1001-aa right as RightTooLong(1001, 1000)") {
      ConstantGapAlignmentScoreProblem.from(protein("A"), protein("A" * 1001)) shouldBe
        Left(ConstantGapAlignmentScoreProblemError.RightTooLong(1001, 1000))
    }

    it("reports LeftTooLong first when both sides exceed the cap (first-failure-wins)") {
      ConstantGapAlignmentScoreProblem.from(protein("A" * 1001), protein("A" * 1001)) shouldBe
        Left(ConstantGapAlignmentScoreProblemError.LeftTooLong(1001, 1000))
    }
  }

  describe("ConstantGapAlignmentScoreProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.ConstantGapAlignmentScoreProblem(protein("A"), protein("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.ConstantGapAlignmentScoreProblem
          |  .from(protein("A"), protein("A")).toOption.get.copy(right = protein("C"))""".stripMargin
      )
    }
  }
}
