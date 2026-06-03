package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FullSpectrumProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("FullSpectrumProblemError") {
    it("constructs InvalidSize carrying the size") {
      val err: FullSpectrumProblemError = FullSpectrumProblemError.InvalidSize(4)
      err shouldBe FullSpectrumProblemError.InvalidSize(4)
    }

    it("constructs NonPositiveMass carrying the index and value") {
      val err: FullSpectrumProblemError = FullSpectrumProblemError.NonPositiveMass(1, -1.0)
      err shouldBe FullSpectrumProblemError.NonPositiveMass(1, -1.0)
    }
  }
}
