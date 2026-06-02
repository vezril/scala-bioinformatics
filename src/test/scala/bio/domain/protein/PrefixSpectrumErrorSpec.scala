package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PrefixSpectrumErrorSpec extends AnyFunSpec with Matchers {

  describe("PrefixSpectrumError") {
    it("constructs EmptySpectrum, TooManyWeights, and NonPositiveWeight as PrefixSpectrumError subtypes") {
      val empty: PrefixSpectrumError = PrefixSpectrumError.EmptySpectrum
      val many: PrefixSpectrumError  = PrefixSpectrumError.TooManyWeights(101, 100)
      val nonPos: PrefixSpectrumError = PrefixSpectrumError.NonPositiveWeight(1, 0.0)

      empty shouldBe PrefixSpectrumError.EmptySpectrum
      many shouldBe PrefixSpectrumError.TooManyWeights(101, 100)
      nonPos shouldBe PrefixSpectrumError.NonPositiveWeight(1, 0.0)
    }
  }
}
