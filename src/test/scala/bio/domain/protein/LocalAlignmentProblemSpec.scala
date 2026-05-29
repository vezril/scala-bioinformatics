package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LocalAlignmentProblemSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  describe("LocalAlignmentProblem.from") {
    it("accepts the canonical Rosalind LOCA sample") {
      val result = LocalAlignmentProblem.from(
        protein("MEANLYPRTEINSTRING"),
        protein("PLEASANTLYEINSTEIN")
      )
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.left.value shouldBe "MEANLYPRTEINSTRING"
      problem.right.value shouldBe "PLEASANTLYEINSTEIN"
    }

    it("accepts two empty strings") {
      LocalAlignmentProblem.from(protein(""), protein("")).isRight shouldBe true
    }

    it("accepts an empty left with a non-empty right") {
      LocalAlignmentProblem.from(protein(""), protein("MEANLY")).isRight shouldBe true
    }

    it("accepts a non-empty left with an empty right") {
      LocalAlignmentProblem.from(protein("PLEASANTLY"), protein("")).isRight shouldBe true
    }

    it("accepts both strings at the 1000-character upper bound") {
      LocalAlignmentProblem.from(protein("A" * 1000), protein("A" * 1000)).isRight shouldBe true
    }

    it("rejects a 1001-character left as LeftTooLong(1001, 1000)") {
      LocalAlignmentProblem.from(protein("A" * 1001), protein("A")) shouldBe
        Left(LocalAlignmentProblemError.LeftTooLong(1001, 1000))
    }

    it("rejects a 1001-character right as RightTooLong(1001, 1000)") {
      LocalAlignmentProblem.from(protein("A"), protein("A" * 1001)) shouldBe
        Left(LocalAlignmentProblemError.RightTooLong(1001, 1000))
    }

    it("reports LeftTooLong first when both sides exceed the cap (first-failure-wins)") {
      LocalAlignmentProblem.from(protein("A" * 1001), protein("A" * 1001)) shouldBe
        Left(LocalAlignmentProblemError.LeftTooLong(1001, 1000))
    }
  }

  describe("LocalAlignmentProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.LocalAlignmentProblem(protein("A"), protein("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.LocalAlignmentProblem
          |  .from(protein("A"), protein("A")).toOption.get.copy(right = protein("C"))""".stripMargin
      )
    }
  }
}
