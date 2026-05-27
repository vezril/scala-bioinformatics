package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CombinationSumProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("CombinationSumProblemError.NegativeN") {
    it("carries the offending value") {
      CombinationSumProblemError.NegativeN(-1).value shouldBe -1
    }
  }

  describe("CombinationSumProblemError.NExceedsMaximum") {
    it("carries the offending value and the maximum") {
      val err = CombinationSumProblemError.NExceedsMaximum(2001, 2000)
      err.value shouldBe 2001
      err.max shouldBe 2000
    }
  }

  describe("CombinationSumProblemError.NegativeM") {
    it("carries the offending value") {
      CombinationSumProblemError.NegativeM(-3).value shouldBe -3
    }
  }

  describe("CombinationSumProblemError.MExceedsN") {
    it("carries both offending inputs") {
      val err = CombinationSumProblemError.MExceedsN(5, 3)
      err.m shouldBe 5
      err.n shouldBe 3
    }
  }
}
