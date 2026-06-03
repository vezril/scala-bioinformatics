package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaximumMatchingProblemErrorSpec extends AnyFunSpec with Matchers {
  describe("MaximumMatchingProblemError.ExceedsMaxLength") {
    it("carries the offending length and the maximum allowed length") {
      val err = MaximumMatchingProblemError.ExceedsMaxLength(150, 100)
      err.length shouldBe 150
      err.max shouldBe 100
    }
  }
}
