package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EditAlignmentSpec extends AnyFunSpec with Matchers {

  describe("EditAlignment") {
    it("constructs with named fields") {
      val a = EditAlignment(distance = 4, augmentedLeft = "PRETTY--", augmentedRight = "PR-TTEIN")
      a.distance shouldBe 4
      a.augmentedLeft shouldBe "PRETTY--"
      a.augmentedRight shouldBe "PR-TTEIN"
    }

    it("is value-equal when all three fields match") {
      EditAlignment(4, "PRETTY--", "PR-TTEIN") shouldBe
        EditAlignment(4, "PRETTY--", "PR-TTEIN")
    }

    it("supports structural sharing via copy") {
      val a = EditAlignment(4, "PRETTY--", "PR-TTEIN")
      a.copy(distance = 5).distance shouldBe 5
      a.copy(distance = 5).augmentedLeft shouldBe "PRETTY--"
      a.copy(distance = 5).augmentedRight shouldBe "PR-TTEIN"
    }

    it("permits the empty/empty degenerate alignment") {
      val a = EditAlignment(0, "", "")
      a.distance shouldBe 0
      a.augmentedLeft shouldBe ""
      a.augmentedRight shouldBe ""
    }
  }
}
