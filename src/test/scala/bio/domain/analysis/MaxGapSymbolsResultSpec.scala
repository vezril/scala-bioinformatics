package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaxGapSymbolsResultSpec extends AnyFunSpec with Matchers {

  describe("MaxGapSymbols result") {
    it("exposes the count") {
      MaxGapSymbols(3).count shouldBe 3
    }

    it("formats the count") {
      MaxGapSymbols(3).format shouldBe "3"
    }
  }
}
