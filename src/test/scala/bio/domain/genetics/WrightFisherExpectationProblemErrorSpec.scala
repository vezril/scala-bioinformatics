package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WrightFisherExpectationProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("WrightFisherExpectationProblemError.NonPositiveN") {
    it("carries the offending value") {
      WrightFisherExpectationProblemError.NonPositiveN(0).value shouldBe 0
    }
  }

  describe("WrightFisherExpectationProblemError.NExceedsMaximum") {
    it("carries the offending value and the maximum") {
      val err = WrightFisherExpectationProblemError.NExceedsMaximum(1000001, 1000000)
      err.value shouldBe 1000001
      err.max shouldBe 1000000
    }
  }

  describe("WrightFisherExpectationProblemError.TooManyProbabilities") {
    it("carries the offending size and the maximum") {
      val err = WrightFisherExpectationProblemError.TooManyProbabilities(21, 20)
      err.size shouldBe 21
      err.max shouldBe 20
    }
  }
}
