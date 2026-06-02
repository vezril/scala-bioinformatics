package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RestrictionMapResultSpec extends AnyFunSpec with Matchers {

  describe("RestrictionMap result") {
    it("exposes the positions") {
      RestrictionMap(Vector(0, 2, 5)).points shouldBe Vector(0, 2, 5)
    }

    it("formats positions space-separated") {
      RestrictionMap(Vector(0, 2, 4, 7, 10)).format shouldBe "0 2 4 7 10"
    }

    it("formats a single-position map") {
      RestrictionMap(Vector(0)).format shouldBe "0"
    }
  }
}
