package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class NamedSequenceSpec extends AnyFunSpec with Matchers {

  describe("NamedSequence") {
    it("constructs with named fields") {
      val ns = NamedSequence(label = "rat", sequence = "AC")
      ns.label shouldBe "rat"
      ns.sequence shouldBe "AC"
    }

    it("is value-equal when both fields match") {
      NamedSequence("rat", "AC") shouldBe NamedSequence("rat", "AC")
    }

    it("supports structural sharing via copy") {
      NamedSequence("rat", "AC").copy(sequence = "TG") shouldBe NamedSequence("rat", "TG")
    }
  }
}
