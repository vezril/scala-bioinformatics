package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EditDistanceAlignmentProblemSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  describe("EditDistanceAlignmentProblem.from") {
    it("accepts the canonical Rosalind EDTA sample") {
      val result = EditDistanceAlignmentProblem.from(protein("PRETTY"), protein("PRTTEIN"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.left.value shouldBe "PRETTY"
      problem.right.value shouldBe "PRTTEIN"
    }

    it("accepts two empty strings") {
      val result = EditDistanceAlignmentProblem.from(protein(""), protein(""))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.left.value shouldBe ""
      problem.right.value shouldBe ""
    }

    it("accepts an empty left with a non-empty right") {
      val result = EditDistanceAlignmentProblem.from(protein(""), protein("MEANLY"))
      result.isRight shouldBe true
    }

    it("accepts a non-empty left with an empty right") {
      val result = EditDistanceAlignmentProblem.from(protein("PLEASANTLY"), protein(""))
      result.isRight shouldBe true
    }

    it("accepts both strings at the 1000-character upper bound") {
      val result = EditDistanceAlignmentProblem.from(protein("A" * 1000), protein("A" * 1000))
      result.isRight shouldBe true
    }

    it("rejects a 1001-character left as LeftTooLong(1001, 1000)") {
      EditDistanceAlignmentProblem.from(protein("A" * 1001), protein("A")) shouldBe
        Left(EditDistanceAlignmentProblemError.LeftTooLong(1001, 1000))
    }

    it("rejects a 1001-character right as RightTooLong(1001, 1000)") {
      EditDistanceAlignmentProblem.from(protein("A"), protein("A" * 1001)) shouldBe
        Left(EditDistanceAlignmentProblemError.RightTooLong(1001, 1000))
    }

    it("reports LeftTooLong first when both sides exceed the cap (first-failure-wins)") {
      EditDistanceAlignmentProblem.from(protein("A" * 1001), protein("A" * 1001)) shouldBe
        Left(EditDistanceAlignmentProblemError.LeftTooLong(1001, 1000))
    }
  }

  describe("EditDistanceAlignmentProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.EditDistanceAlignmentProblem(protein("A"), protein("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.EditDistanceAlignmentProblem
          |  .from(protein("A"), protein("A")).toOption.get.copy(right = protein("C"))""".stripMargin
      )
    }
  }
}
