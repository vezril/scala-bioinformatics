package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TransitionTransversionRatioSpec extends AnyFunSpec with Matchers {
  describe("TransitionTransversionRatio") {
    it("computes the ratio from the counts and formats to 11 decimals") {
      val r = TransitionTransversionRatio(17, 14)
      r.ratio shouldBe 1.2142857 +- 0.0001
      r.format shouldBe "1.21428571429"
    }

    it("defines the ratio as zero when there are no transversions") {
      TransitionTransversionRatio(5, 0).ratio shouldBe 0.0
    }
  }
}
