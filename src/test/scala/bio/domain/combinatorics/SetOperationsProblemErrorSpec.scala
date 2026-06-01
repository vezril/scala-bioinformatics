package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SetOperationsProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("SetOperationsProblemError") {
    it("carries the offending universe size in NonPositiveUniverse") {
      SetOperationsProblemError.NonPositiveUniverse(0).value shouldBe 0
    }

    it("carries the offending size and the maximum in ExceedsMaximum") {
      val err = SetOperationsProblemError.ExceedsMaximum(20001, 20000)
      err.value shouldBe 20001
      err.max shouldBe 20000
    }

    it("carries the subset label, element value, and universe in ElementOutOfRange") {
      val err = SetOperationsProblemError.ElementOutOfRange("B", 6, 5)
      err.setLabel shouldBe "B"
      err.value shouldBe 6
      err.universe shouldBe 5
    }
  }
}
