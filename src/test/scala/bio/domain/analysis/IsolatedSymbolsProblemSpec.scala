package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class IsolatedSymbolsProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("IsolatedSymbolsProblem.from") {
    it("accepts the canonical Rosalind OSYM sample") {
      val result = IsolatedSymbolsProblem.from(dna("ATAGATA"), dna("ACAGGTA"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.left.value shouldBe "ATAGATA"
      problem.right.value shouldBe "ACAGGTA"
    }

    it("accepts two empty strings") {
      IsolatedSymbolsProblem.from(dna(""), dna("")).isRight shouldBe true
    }

    it("accepts an empty left with a non-empty right") {
      IsolatedSymbolsProblem.from(dna(""), dna("GATTACA")).isRight shouldBe true
    }

    it("accepts a non-empty left with an empty right") {
      IsolatedSymbolsProblem.from(dna("GATTACA"), dna("")).isRight shouldBe true
    }

    it("accepts both strings at the 1000-bp upper bound") {
      IsolatedSymbolsProblem
        .from(dna("A" * 1000), dna("A" * 1000))
        .isRight shouldBe true
    }

    it("rejects a 1001-bp left as LeftTooLong(1001, 1000)") {
      IsolatedSymbolsProblem.from(dna("A" * 1001), dna("A")) shouldBe
        Left(IsolatedSymbolsProblemError.LeftTooLong(1001, 1000))
    }

    it("rejects a 1001-bp right as RightTooLong(1001, 1000)") {
      IsolatedSymbolsProblem.from(dna("A"), dna("A" * 1001)) shouldBe
        Left(IsolatedSymbolsProblemError.RightTooLong(1001, 1000))
    }

    it("reports LeftTooLong first when both sides exceed the cap (first-failure-wins)") {
      IsolatedSymbolsProblem.from(dna("A" * 1001), dna("A" * 1001)) shouldBe
        Left(IsolatedSymbolsProblemError.LeftTooLong(1001, 1000))
    }
  }

  describe("IsolatedSymbolsProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.IsolatedSymbolsProblem(dna("A"), dna("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.IsolatedSymbolsProblem
          |  .from(dna("A"), dna("A")).toOption.get.copy(right = dna("C"))""".stripMargin
      )
    }
  }
}
