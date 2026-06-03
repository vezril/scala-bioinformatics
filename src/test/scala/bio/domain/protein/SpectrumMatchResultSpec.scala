package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SpectrumMatchResultSpec extends AnyFunSpec with Matchers {

  describe("SpectrumMatch result") {
    it("exposes the multiplicity and protein") {
      val r = SpectrumMatch(3, "IASWMQS")
      r.multiplicity shouldBe 3
      r.protein shouldBe "IASWMQS"
    }

    it("formats the multiplicity then the protein") {
      SpectrumMatch(3, "IASWMQS").format shouldBe "3\nIASWMQS"
    }
  }
}
