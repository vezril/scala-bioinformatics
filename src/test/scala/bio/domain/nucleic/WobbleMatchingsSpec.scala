package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WobbleMatchingsSpec extends AnyFunSpec with Matchers {
  describe("WobbleMatchings.format") {
    it("renders the count as its decimal string") {
      WobbleMatchings(BigInt("284850219977421")).format shouldBe "284850219977421"
    }
  }
}
