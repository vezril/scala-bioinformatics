package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MotifLocationsSpec extends AnyFunSpec with Matchers {
  describe("MotifLocations.format") {
    it("renders the id and positions on separate lines") {
      MotifLocations("B5ZC00", Vector(85, 118, 142)).format shouldBe "B5ZC00\n85 118 142"
    }
  }
}
