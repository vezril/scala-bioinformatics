package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RandomMotifMatchResultSpec extends AnyFunSpec with Matchers {

  describe("RandomMotifMatch") {
    it("exposes the probability") {
      RandomMotifMatch(0.25).probability shouldBe 0.25
    }

    it("formats to three decimal places") {
      RandomMotifMatch(0.5).format shouldBe "0.500"
    }

    it("rounds to three decimal places") {
      RandomMotifMatch(0.1234).format shouldBe "0.123"
    }
  }
}
