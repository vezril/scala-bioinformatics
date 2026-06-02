package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CarrierProbabilitiesResultSpec extends AnyFunSpec with Matchers {

  describe("CarrierProbabilities result") {
    it("exposes the carrier probabilities") {
      val values = Vector(0.18, 0.5, 0.32)
      CarrierProbabilities(values).values shouldBe values
    }

    it("formats values space-separated to three decimals") {
      CarrierProbabilities(Vector(0.18, 0.5, 0.32)).format shouldBe "0.180 0.500 0.320"
    }

    it("renders the empty result as the empty string") {
      CarrierProbabilities(Vector.empty).format shouldBe ""
    }
  }
}
