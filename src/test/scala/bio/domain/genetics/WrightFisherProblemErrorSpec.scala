package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WrightFisherProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("WrightFisherProblemError.NonPositiveN") {
    it("carries the offending value") {
      WrightFisherProblemError.NonPositiveN(0).value shouldBe 0
    }
  }

  describe("WrightFisherProblemError.NExceedsMaximum") {
    it("carries the offending value and the maximum") {
      val err = WrightFisherProblemError.NExceedsMaximum(8, 7)
      err.value shouldBe 8
      err.max shouldBe 7
    }
  }

  describe("WrightFisherProblemError.NonPositiveM") {
    it("carries the offending value") {
      WrightFisherProblemError.NonPositiveM(0).value shouldBe 0
    }
  }

  describe("WrightFisherProblemError.MExceedsTotalAlleles") {
    it("carries the offending value and the computed maximum (2n)") {
      val err = WrightFisherProblemError.MExceedsTotalAlleles(9, 8)
      err.value shouldBe 9
      err.max shouldBe 8
    }
  }

  describe("WrightFisherProblemError.NonPositiveG") {
    it("carries the offending value") {
      WrightFisherProblemError.NonPositiveG(0).value shouldBe 0
    }
  }

  describe("WrightFisherProblemError.GExceedsMaximum") {
    it("carries the offending value and the maximum") {
      val err = WrightFisherProblemError.GExceedsMaximum(7, 6)
      err.value shouldBe 7
      err.max shouldBe 6
    }
  }

  describe("WrightFisherProblemError.NonPositiveK") {
    it("carries the offending value") {
      WrightFisherProblemError.NonPositiveK(0).value shouldBe 0
    }
  }

  describe("WrightFisherProblemError.KExceedsTotalAlleles") {
    it("carries the offending value and the computed maximum (2n)") {
      val err = WrightFisherProblemError.KExceedsTotalAlleles(9, 8)
      err.value shouldBe 9
      err.max shouldBe 8
    }
  }
}
