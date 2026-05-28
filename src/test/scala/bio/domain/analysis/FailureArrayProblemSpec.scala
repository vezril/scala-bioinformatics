package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FailureArrayProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("FailureArrayProblem.from") {
    it("accepts the canonical 21-character Rosalind KMP sample") {
      val result = FailureArrayProblem.from(dna("CAGCATGGTATCACAGCAGAG"))
      result.isRight shouldBe true
      result.toOption.get.dna.value shouldBe "CAGCATGGTATCACAGCAGAG"
    }

    it("accepts a single-character DNA string") {
      val result = FailureArrayProblem.from(dna("A"))
      result.isRight shouldBe true
      result.toOption.get.dna.value shouldBe "A"
    }

    it("rejects an empty DNA string as EmptySequence") {
      FailureArrayProblem.from(dna("")) shouldBe
        Left(FailureArrayProblemError.EmptySequence)
    }
  }

  describe("FailureArrayProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.FailureArrayProblem(dna("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.FailureArrayProblem.from(dna("A")).toOption.get.copy(dna = dna("C"))"""
      )
    }
  }
}
