package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AffineGapAlignmentProblemSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  describe("AffineGapAlignmentProblem.from") {
    it("accepts the canonical Rosalind GAFF sample") {
      val result =
        AffineGapAlignmentProblem.from(protein("PRTEINS"), protein("PRTWPSEIN"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.left.value shouldBe "PRTEINS"
      problem.right.value shouldBe "PRTWPSEIN"
    }

    it("accepts two empty strings") {
      AffineGapAlignmentProblem.from(protein(""), protein("")).isRight shouldBe true
    }

    it("accepts an empty left with a non-empty right") {
      AffineGapAlignmentProblem.from(protein(""), protein("MEANLY")).isRight shouldBe true
    }

    it("accepts a non-empty left with an empty right") {
      AffineGapAlignmentProblem.from(protein("PRTEINS"), protein("")).isRight shouldBe true
    }

    it("accepts both strings at the 100-aa upper bound") {
      AffineGapAlignmentProblem
        .from(protein("A" * 100), protein("A" * 100))
        .isRight shouldBe true
    }

    it("rejects a 101-aa left as LeftTooLong(101, 100)") {
      AffineGapAlignmentProblem.from(protein("A" * 101), protein("A")) shouldBe
        Left(AffineGapAlignmentProblemError.LeftTooLong(101, 100))
    }

    it("rejects a 101-aa right as RightTooLong(101, 100)") {
      AffineGapAlignmentProblem.from(protein("A"), protein("A" * 101)) shouldBe
        Left(AffineGapAlignmentProblemError.RightTooLong(101, 100))
    }

    it("reports LeftTooLong first when both sides exceed the cap (first-failure-wins)") {
      AffineGapAlignmentProblem.from(protein("A" * 101), protein("A" * 101)) shouldBe
        Left(AffineGapAlignmentProblemError.LeftTooLong(101, 100))
    }
  }

  describe("AffineGapAlignmentProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.AffineGapAlignmentProblem(protein("A"), protein("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.AffineGapAlignmentProblem
          |  .from(protein("A"), protein("A")).toOption.get.copy(right = protein("C"))""".stripMargin
      )
    }
  }
}
