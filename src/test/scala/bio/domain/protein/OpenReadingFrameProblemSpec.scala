package bio.domain.protein

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OpenReadingFrameProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("OpenReadingFrameProblem.from") {
    it("accepts a DNA string within the length limit, preserving the DNA") {
      val sample = dna("AGCCATGTAGCTAACTCAGGT") // length 21
      OpenReadingFrameProblem.from(sample).map(_.dna) shouldBe Right(sample)
    }

    it("accepts an empty sequence") {
      OpenReadingFrameProblem.from(dna("")).isRight shouldBe true
    }

    it("accepts a sequence at the 1000 bp upper bound") {
      OpenReadingFrameProblem.from(dna("A" * 1000)).isRight shouldBe true
    }

    it("rejects a sequence longer than 1000 bp") {
      OpenReadingFrameProblem.from(dna("A" * 1001)) shouldBe Left(
        OpenReadingFrameProblemError.SequenceTooLong(1001, 1000)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.OpenReadingFrameProblem(bio.domain.nucleic.DnaString.from("A").toOption.get)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.OpenReadingFrameProblem.from(bio.domain.nucleic.DnaString.from("A").toOption.get).toOption.get.copy()"""
      )
    }
  }
}
