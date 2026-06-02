package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LongestRepeatResultSpec extends AnyFunSpec with Matchers {

  describe("LongestRepeat result") {
    it("exposes the substring") {
      LongestRepeat("CATAC").substring shouldBe "CATAC"
    }

    it("formats the substring verbatim") {
      LongestRepeat("CATAC").format shouldBe "CATAC"
    }

    it("renders the empty result as the empty string") {
      LongestRepeat("").format shouldBe ""
    }
  }
}
