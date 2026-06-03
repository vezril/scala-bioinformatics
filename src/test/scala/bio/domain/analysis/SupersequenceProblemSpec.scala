package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SupersequenceProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("SupersequenceProblem.from") {
    it("accepts two DNA strings within the length limit, preserving them") {
      val s = dna("ATCTGAT")
      val t = dna("TGCATA")
      val result = SupersequenceProblem.from(s, t)
      result.map(_.s) shouldBe Right(s)
      result.map(_.t) shouldBe Right(t)
    }

    it("rejects a first sequence longer than 1000 bp") {
      SupersequenceProblem.from(dna("A" * 1001), dna("ACGT")) shouldBe Left(
        SupersequenceProblemError.SequenceTooLong(1001, 1000)
      )
    }

    it("rejects a second sequence longer than 1000 bp") {
      SupersequenceProblem.from(dna("ACGT"), dna("A" * 1001)) shouldBe Left(
        SupersequenceProblemError.SequenceTooLong(1001, 1000)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.SupersequenceProblem(bio.domain.nucleic.DnaString.from("A").toOption.get, bio.domain.nucleic.DnaString.from("C").toOption.get)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.SupersequenceProblem.from(bio.domain.nucleic.DnaString.from("A").toOption.get, bio.domain.nucleic.DnaString.from("C").toOption.get).toOption.get.copy()"""
      )
    }
  }
}
