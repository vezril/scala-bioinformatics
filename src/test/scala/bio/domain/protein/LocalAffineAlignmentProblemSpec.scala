package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LocalAffineAlignmentProblemSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  describe("LocalAffineAlignmentProblem.from") {
    it("accepts the canonical Rosalind LAFF sample") {
      val result =
        LocalAffineAlignmentProblem.from(protein("PLEASANTLY"), protein("MEANLY"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.left.value shouldBe "PLEASANTLY"
      problem.right.value shouldBe "MEANLY"
    }

    it("accepts two empty strings") {
      LocalAffineAlignmentProblem.from(protein(""), protein("")).isRight shouldBe true
    }

    it("accepts an empty left with a non-empty right") {
      LocalAffineAlignmentProblem.from(protein(""), protein("MEANLY")).isRight shouldBe true
    }

    it("accepts a non-empty left with an empty right") {
      LocalAffineAlignmentProblem.from(protein("PLEASANTLY"), protein("")).isRight shouldBe true
    }

    it("accepts both strings at the 10,000-aa upper bound") {
      LocalAffineAlignmentProblem
        .from(protein("A" * 10000), protein("A" * 10000))
        .isRight shouldBe true
    }

    it("rejects a 10,001-aa left as LeftTooLong(10001, 10000)") {
      LocalAffineAlignmentProblem.from(protein("A" * 10001), protein("A")) shouldBe
        Left(LocalAffineAlignmentProblemError.LeftTooLong(10001, 10000))
    }

    it("rejects a 10,001-aa right as RightTooLong(10001, 10000)") {
      LocalAffineAlignmentProblem.from(protein("A"), protein("A" * 10001)) shouldBe
        Left(LocalAffineAlignmentProblemError.RightTooLong(10001, 10000))
    }

    it("reports LeftTooLong first when both sides exceed the cap (first-failure-wins)") {
      LocalAffineAlignmentProblem.from(protein("A" * 10001), protein("A" * 10001)) shouldBe
        Left(LocalAffineAlignmentProblemError.LeftTooLong(10001, 10000))
    }
  }

  describe("LocalAffineAlignmentProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.LocalAffineAlignmentProblem(protein("A"), protein("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.LocalAffineAlignmentProblem
          |  .from(protein("A"), protein("A")).toOption.get.copy(right = protein("C"))""".stripMargin
      )
    }
  }
}
