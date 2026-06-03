package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaximumMatchingsSpec extends AnyFunSpec with Matchers {
  describe("MaximumMatchings.format") {
    it("renders the count as its decimal string") {
      MaximumMatchings(BigInt(6)).format shouldBe "6"
    }
  }
}
