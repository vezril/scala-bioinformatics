package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SignedPermutationProblemErrorSpec extends AnyFunSpec with Matchers {
  describe("SignedPermutationProblemError.ExceedsMaximum") {
    it("carries the offending length and the maximum allowed") {
      val err = SignedPermutationProblemError.ExceedsMaximum(9, 6)
      err.n shouldBe 9
      err.max shouldBe 6
    }
  }
}
