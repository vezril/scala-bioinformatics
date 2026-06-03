package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PermutationErrorSpec extends AnyFunSpec with Matchers {
  describe("PermutationError.TooLong") {
    it("carries the offending length and the maximum allowed") {
      val err = PermutationError.TooLong(12000, 10000)
      err.length shouldBe 12000
      err.max shouldBe 10000
    }
  }
}
