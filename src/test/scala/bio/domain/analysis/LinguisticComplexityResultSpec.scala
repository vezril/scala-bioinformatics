package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LinguisticComplexityResultSpec extends AnyFunSpec with Matchers {

  describe("LinguisticComplexity result") {
    it("exposes the value") {
      LinguisticComplexity(0.875).value shouldBe 0.875
    }

    it("formats to three decimal places") {
      LinguisticComplexity(0.875).format shouldBe "0.875"
    }

    it("rounds to three decimal places") {
      LinguisticComplexity(0.4).format shouldBe "0.400"
    }
  }
}
