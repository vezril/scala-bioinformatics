package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SubsetUniverseSizeErrorSpec extends AnyFunSpec with Matchers {

  describe("SubsetUniverseSizeError.NonPositive") {
    it("carries the offending value (zero)") {
      SubsetUniverseSizeError.NonPositive(0).value shouldBe 0
    }

    it("carries the offending value (negative)") {
      SubsetUniverseSizeError.NonPositive(-5).value shouldBe -5
    }
  }

  describe("SubsetUniverseSizeError.ExceedsMaximum") {
    it("carries the offending value and the maximum") {
      val err = SubsetUniverseSizeError.ExceedsMaximum(1001, 1000)
      err.value shouldBe 1001
      err.max shouldBe 1000
    }
  }
}
