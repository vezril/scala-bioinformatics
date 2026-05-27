package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WrightFisherFixationProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("WrightFisherFixationProblemError.NonPositiveN") {
    it("carries the offending value") {
      WrightFisherFixationProblemError.NonPositiveN(0).value shouldBe 0
    }
  }

  describe("WrightFisherFixationProblemError.NExceedsMaximum") {
    it("carries the offending value and the maximum") {
      val err = WrightFisherFixationProblemError.NExceedsMaximum(101, 100)
      err.value shouldBe 101
      err.max shouldBe 100
    }
  }

  describe("WrightFisherFixationProblemError.NonPositiveM") {
    it("carries the offending value") {
      WrightFisherFixationProblemError.NonPositiveM(0).value shouldBe 0
    }
  }

  describe("WrightFisherFixationProblemError.MExceedsMaximum") {
    it("carries the offending value and the maximum") {
      val err = WrightFisherFixationProblemError.MExceedsMaximum(101, 100)
      err.value shouldBe 101
      err.max shouldBe 100
    }
  }

  describe("WrightFisherFixationProblemError.TooManyRecessiveCounts") {
    it("carries the offending size and the maximum") {
      val err = WrightFisherFixationProblemError.TooManyRecessiveCounts(101, 100)
      err.size shouldBe 101
      err.max shouldBe 100
    }
  }

  describe("WrightFisherFixationProblemError.RecessiveCountOutOfRange") {
    it("carries the offending index, value, and computed maximum (2n)") {
      val err = WrightFisherFixationProblemError.RecessiveCountOutOfRange(2, 9, 8)
      err.index shouldBe 2
      err.value shouldBe 9
      err.max shouldBe 8
    }
  }
}
