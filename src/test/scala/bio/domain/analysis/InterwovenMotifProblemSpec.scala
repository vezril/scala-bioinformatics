package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InterwovenMotifProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(fail(s"invalid DNA string: $s"))

  describe("InterwovenMotifProblem.from") {
    it("accepts a valid text and pattern set") {
      val text     = dna("GACCACGGTT")
      val patterns = Vector(dna("ACAG"), dna("GT"), dna("CCG"))
      val result   = InterwovenMotifProblem.from(text, patterns)
      result.map(_.text) shouldBe Right(text)
      result.map(_.patterns) shouldBe Right(patterns)
    }

    it("accepts an empty pattern list") {
      InterwovenMotifProblem
        .from(dna("ACGT"), Vector.empty)
        .map(_.patterns) shouldBe Right(Vector.empty[DnaString])
    }

    it("rejects more than ten patterns") {
      val patterns = Vector.fill(11)(dna("A"))
      InterwovenMotifProblem.from(dna("ACGT"), patterns) shouldBe Left(
        InterwovenMotifProblemError.TooManyPatterns(11, 10)
      )
    }

    it("rejects a text longer than the bound") {
      val text = dna("A" * 10001)
      InterwovenMotifProblem.from(text, Vector(dna("AC"))) shouldBe Left(
        InterwovenMotifProblemError.TextTooLong(10001, 10000)
      )
    }

    it("rejects a pattern longer than the bound") {
      val patterns = Vector(dna("AC"), dna("A" * 11))
      InterwovenMotifProblem.from(dna("ACGT"), patterns) shouldBe Left(
        InterwovenMotifProblemError.PatternTooLong(11, 10)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.InterwovenMotifProblem(bio.domain.nucleic.DnaString.from("ACGT").toOption.get, Vector.empty)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """InterwovenMotifProblem.from(DnaString.from("ACGT").toOption.get, Vector.empty).toOption.get.copy()"""
      )
    }
  }
}
