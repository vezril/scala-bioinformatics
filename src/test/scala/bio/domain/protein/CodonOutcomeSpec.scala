package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CodonOutcomeSpec extends AnyFunSpec with Matchers {

  describe("CodonOutcome") {
    it("AminoAcidProduct exposes the wrapped amino acid") {
      CodonOutcome.AminoAcidProduct(AminoAcid.M).aa shouldBe AminoAcid.M
    }

    it("Stop is a subtype of CodonOutcome") {
      val outcome: CodonOutcome = CodonOutcome.Stop
      outcome shouldBe CodonOutcome.Stop
    }

    it("AminoAcidProduct and Stop are distinct subtypes") {
      val product: CodonOutcome = CodonOutcome.AminoAcidProduct(AminoAcid.M)
      val stop: CodonOutcome    = CodonOutcome.Stop
      product should not be stop
    }
  }
}
