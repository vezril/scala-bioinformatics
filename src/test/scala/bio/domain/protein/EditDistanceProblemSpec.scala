package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EditDistanceProblemSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  describe("EditDistanceProblem.from") {
    it("accepts the canonical Rosalind EDIT sample") {
      val result = EditDistanceProblem.from(protein("PLEASANTLY"), protein("MEANLY"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.left.value shouldBe "PLEASANTLY"
      problem.right.value shouldBe "MEANLY"
    }

    it("accepts two empty strings") {
      val result = EditDistanceProblem.from(protein(""), protein(""))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.left.value shouldBe ""
      problem.right.value shouldBe ""
    }

    it("accepts an empty left with a non-empty right") {
      val result = EditDistanceProblem.from(protein(""), protein("MEANLY"))
      result.isRight shouldBe true
    }

    it("accepts a non-empty left with an empty right") {
      val result = EditDistanceProblem.from(protein("PLEASANTLY"), protein(""))
      result.isRight shouldBe true
    }

    it("accepts both strings at the 1000-character upper bound") {
      val result = EditDistanceProblem.from(protein("A" * 1000), protein("A" * 1000))
      result.isRight shouldBe true
    }

    it("rejects a 1001-character left as LeftTooLong(1001, 1000)") {
      EditDistanceProblem.from(protein("A" * 1001), protein("A")) shouldBe
        Left(EditDistanceProblemError.LeftTooLong(1001, 1000))
    }

    it("rejects a 1001-character right as RightTooLong(1001, 1000)") {
      EditDistanceProblem.from(protein("A"), protein("A" * 1001)) shouldBe
        Left(EditDistanceProblemError.RightTooLong(1001, 1000))
    }

    it("reports LeftTooLong first when both sides exceed the cap (first-failure-wins)") {
      EditDistanceProblem.from(protein("A" * 1001), protein("A" * 1001)) shouldBe
        Left(EditDistanceProblemError.LeftTooLong(1001, 1000))
    }
  }

  describe("EditDistanceProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.EditDistanceProblem(protein("A"), protein("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.EditDistanceProblem
          |  .from(protein("A"), protein("A")).toOption.get.copy(right = protein("C"))""".stripMargin
      )
    }
  }
}
