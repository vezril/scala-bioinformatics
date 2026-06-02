package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProteinMassProblemSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  describe("ProteinMassProblem.from") {
    it("accepts the canonical Rosalind PRTM sample protein") {
      val result = ProteinMassProblem.from(protein("SKADYEK"))
      result.isRight shouldBe true
      result.toOption.get.protein.value shouldBe "SKADYEK"
    }

    it("accepts an empty protein") {
      ProteinMassProblem.from(protein("")).isRight shouldBe true
    }

    it("accepts a protein at the 1000-aa upper bound") {
      ProteinMassProblem.from(protein("A" * 1000)).isRight shouldBe true
    }

    it("rejects a 1001-aa protein as ProteinTooLong(1001, 1000)") {
      ProteinMassProblem.from(protein("A" * 1001)) shouldBe
        Left(ProteinMassProblemError.ProteinTooLong(1001, 1000))
    }
  }

  describe("ProteinMassProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.ProteinMassProblem(protein("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.ProteinMassProblem
          |  .from(protein("A")).toOption.get.copy(protein = protein("C"))""".stripMargin
      )
    }
  }
}
