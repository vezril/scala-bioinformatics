package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MultipleAlignmentSpec extends AnyFunSpec with Matchers {

  describe("MultipleAlignment (domain ADT)") {
    it("constructs with named fields") {
      val a = MultipleAlignment(
        score = -18,
        augmentedStrings = Vector("ATAT-CCG", "-T---CCG", "ATGTACTG", "ATGT-CTG")
      )
      a.score shouldBe -18
      a.augmentedStrings shouldBe Vector("ATAT-CCG", "-T---CCG", "ATGTACTG", "ATGT-CTG")
    }

    it("is value-equal when both fields match") {
      MultipleAlignment(-18, Vector("ATAT-CCG", "-T---CCG", "ATGTACTG", "ATGT-CTG")) shouldBe
        MultipleAlignment(-18, Vector("ATAT-CCG", "-T---CCG", "ATGTACTG", "ATGT-CTG"))
    }

    it("supports structural sharing via copy") {
      val a = MultipleAlignment(-18, Vector("ATAT-CCG", "-T---CCG", "ATGTACTG", "ATGT-CTG"))
      val b = a.copy(score = -10)
      b.score shouldBe -10
      b.augmentedStrings shouldBe a.augmentedStrings
    }

    it("permits the empty/empty/empty/empty degenerate alignment") {
      val a = MultipleAlignment(0, Vector("", "", "", ""))
      a.score shouldBe 0
      a.augmentedStrings shouldBe Vector("", "", "", "")
    }
  }
}
