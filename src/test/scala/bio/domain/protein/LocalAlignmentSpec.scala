package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LocalAlignmentSpec extends AnyFunSpec with Matchers {

  describe("LocalAlignment (domain ADT)") {
    it("constructs with named fields") {
      val la = LocalAlignment(
        score = 23,
        leftSubstring = "LYPRTEINSTRIN",
        rightSubstring = "LYEINSTEIN"
      )
      la.score shouldBe 23
      la.leftSubstring shouldBe "LYPRTEINSTRIN"
      la.rightSubstring shouldBe "LYEINSTEIN"
    }

    it("is value-equal when all three fields match") {
      LocalAlignment(23, "LY", "LY") shouldBe LocalAlignment(23, "LY", "LY")
    }

    it("supports structural sharing via copy") {
      val la = LocalAlignment(23, "LY", "LY")
      la.copy(score = 0).score shouldBe 0
      la.copy(score = 0).leftSubstring shouldBe "LY"
    }

    it("permits the degenerate empty result") {
      val la = LocalAlignment(0, "", "")
      la.score shouldBe 0
      la.leftSubstring shouldBe ""
      la.rightSubstring shouldBe ""
    }
  }
}
