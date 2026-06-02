package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaximalRepeatsResultSpec extends AnyFunSpec with Matchers {

  describe("MaximalRepeats result") {
    it("exposes the repeats") {
      val repeats = Vector("AG", "TAG")
      MaximalRepeats(repeats).repeats shouldBe repeats
    }

    it("formats one repeat per line") {
      MaximalRepeats(Vector("AG", "TAG")).format shouldBe "AG\nTAG"
    }

    it("renders the empty result as the empty string") {
      MaximalRepeats(Vector.empty).format shouldBe ""
    }
  }
}
