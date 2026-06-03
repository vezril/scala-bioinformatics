package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SpectrumGraphProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("SpectrumGraphProblemError") {
    it("constructs TooManyMasses carrying size and max") {
      val err: SpectrumGraphProblemError = SpectrumGraphProblemError.TooManyMasses(101, 100)
      err shouldBe SpectrumGraphProblemError.TooManyMasses(101, 100)
    }

    it("constructs NonPositiveMass carrying the index and value") {
      val err: SpectrumGraphProblemError = SpectrumGraphProblemError.NonPositiveMass(1, -1.0)
      err shouldBe SpectrumGraphProblemError.NonPositiveMass(1, -1.0)
    }
  }
}
