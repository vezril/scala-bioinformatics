package bio.domain.graph

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaximalRepeatProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("MaximalRepeatProblem.from") {
    it("accepts a DNA string and minimum length within bounds, preserving them") {
      val sample = dna("A" * 25)
      val result = MaximalRepeatProblem.from(sample, 20)
      result.map(_.dna) shouldBe Right(sample)
      result.map(_.minLength) shouldBe Right(20)
    }

    it("rejects a DNA string longer than 1000 bp") {
      MaximalRepeatProblem.from(dna("A" * 1001), 20) shouldBe Left(
        MaximalRepeatProblemError.SequenceTooLong(1001, 1000)
      )
    }

    it("rejects a non-positive minimum length") {
      MaximalRepeatProblem.from(dna("ACGT"), 0) shouldBe Left(
        MaximalRepeatProblemError.NonPositiveMinLength(0)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.MaximalRepeatProblem(bio.domain.nucleic.DnaString.from("ACGT").toOption.get, 20)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.MaximalRepeatProblem.from(bio.domain.nucleic.DnaString.from("ACGT").toOption.get, 20).toOption.get.copy()"""
      )
    }
  }
}
