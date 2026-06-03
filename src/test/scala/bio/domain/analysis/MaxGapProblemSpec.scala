package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaxGapProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("MaxGapProblem.from") {
    it("accepts two DNA strings within the length limit, preserving them") {
      val s = dna("AACGTA")
      val t = dna("ACACCTA")
      val result = MaxGapProblem.from(s, t)
      result.map(_.s) shouldBe Right(s)
      result.map(_.t) shouldBe Right(t)
    }

    it("rejects a first sequence longer than 5000 bp") {
      MaxGapProblem.from(dna("A" * 5001), dna("ACGT")) shouldBe Left(
        MaxGapProblemError.SequenceTooLong(5001, 5000)
      )
    }

    it("rejects a second sequence longer than 5000 bp") {
      MaxGapProblem.from(dna("ACGT"), dna("A" * 5001)) shouldBe Left(
        MaxGapProblemError.SequenceTooLong(5001, 5000)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.MaxGapProblem(bio.domain.nucleic.DnaString.from("A").toOption.get, bio.domain.nucleic.DnaString.from("C").toOption.get)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.MaxGapProblem.from(bio.domain.nucleic.DnaString.from("A").toOption.get, bio.domain.nucleic.DnaString.from("C").toOption.get).toOption.get.copy()"""
      )
    }
  }
}
