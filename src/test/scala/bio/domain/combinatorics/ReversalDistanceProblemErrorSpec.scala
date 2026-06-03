package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversalDistanceProblemErrorSpec extends AnyFunSpec with Matchers {
  describe("ReversalDistanceProblemError.LengthMismatch") {
    it("carries the two differing lengths") {
      val err = ReversalDistanceProblemError.LengthMismatch(5, 8)
      err.sourceLength shouldBe 5
      err.targetLength shouldBe 8
    }
  }
}
