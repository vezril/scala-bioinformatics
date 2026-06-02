package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RestrictionSiteProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("RestrictionSiteProblem.from") {
    it("accepts the canonical sample sequence, preserving the DNA") {
      val sample = dna("TCAATGCATGCGGGTCTATATGCAT")
      RestrictionSiteProblem.from(sample).map(_.dna) shouldBe Right(sample)
    }

    it("accepts an empty sequence") {
      RestrictionSiteProblem.from(dna("")).isRight shouldBe true
    }

    it("accepts a sequence at the 1000 bp upper bound") {
      RestrictionSiteProblem.from(dna("A" * 1000)).isRight shouldBe true
    }

    it("rejects a sequence longer than 1000 bp") {
      RestrictionSiteProblem.from(dna("A" * 1001)) shouldBe Left(
        RestrictionSiteProblemError.SequenceTooLong(1001, 1000)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.nucleic.RestrictionSiteProblem(bio.domain.nucleic.DnaString.from("A").toOption.get)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.nucleic.RestrictionSiteProblem.from(bio.domain.nucleic.DnaString.from("A").toOption.get).toOption.get.copy()"""
      )
    }
  }
}
