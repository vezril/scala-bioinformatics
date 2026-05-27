package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OverlapLengthErrorSpec extends AnyFunSpec with Matchers {

  describe("OverlapLengthError.NonPositive") {
    it("carries the offending value (zero)") {
      OverlapLengthError.NonPositive(0).value shouldBe 0
    }

    it("carries the offending value (negative)") {
      OverlapLengthError.NonPositive(-3).value shouldBe -3
    }
  }
}
