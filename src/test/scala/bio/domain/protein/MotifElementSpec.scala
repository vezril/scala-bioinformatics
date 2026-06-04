package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MotifElementSpec extends AnyFunSpec with Matchers {
  describe("MotifElement.OneOf") {
    it("matches a listed residue and rejects an unlisted one") {
      val el = MotifElement.OneOf(Set('S', 'T'))
      el.matches('S') shouldBe true
      el.matches('A') shouldBe false
    }
  }

  describe("MotifElement.NoneOf") {
    it("rejects a listed residue and matches an unlisted one") {
      val el = MotifElement.NoneOf(Set('P'))
      el.matches('P') shouldBe false
      el.matches('A') shouldBe true
    }
  }
}
