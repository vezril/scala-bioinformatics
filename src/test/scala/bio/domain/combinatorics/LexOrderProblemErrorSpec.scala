package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LexOrderProblemErrorSpec extends AnyFunSpec with Matchers {
  describe("LexOrderProblemError.TooManySymbols") {
    it("carries the offending count and the maximum allowed") {
      val err = LexOrderProblemError.TooManySymbols(15, 12)
      err.count shouldBe 15
      err.max shouldBe 12
    }
  }
}
