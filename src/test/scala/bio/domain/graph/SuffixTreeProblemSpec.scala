package bio.domain.graph

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SuffixTreeProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("SuffixTreeProblem.from") {
    it("accepts a DNA string within the length limit, preserving the DNA") {
      val sample = dna("ATAAATG")
      SuffixTreeProblem.from(sample).map(_.dna) shouldBe Right(sample)
    }

    it("accepts an empty DNA string") {
      SuffixTreeProblem.from(dna("")).isRight shouldBe true
    }

    it("accepts a DNA string at the 1000 bp upper bound") {
      SuffixTreeProblem.from(dna("A" * 1000)).isRight shouldBe true
    }

    it("rejects a DNA string longer than 1000 bp") {
      SuffixTreeProblem.from(dna("A" * 1001)) shouldBe Left(
        SuffixTreeProblemError.SequenceTooLong(1001, 1000)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.SuffixTreeProblem(bio.domain.nucleic.DnaString.from("A").toOption.get)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.SuffixTreeProblem.from(bio.domain.nucleic.DnaString.from("A").toOption.get).toOption.get.copy()"""
      )
    }
  }
}
