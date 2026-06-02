package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LinguisticComplexityProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("LinguisticComplexityProblem") {
    it("wraps and exposes the DNA string") {
      val sample = dna("ATTTGGATT")
      LinguisticComplexityProblem(sample).dna shouldBe sample
    }

    it("accepts an empty DNA string") {
      LinguisticComplexityProblem(dna("")).dna shouldBe dna("")
    }
  }
}
