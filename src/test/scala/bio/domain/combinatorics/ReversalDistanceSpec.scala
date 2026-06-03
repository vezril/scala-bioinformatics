package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversalDistanceSpec extends AnyFunSpec with Matchers {
  describe("ReversalDistance.format") {
    it("renders the distance as its decimal string") {
      ReversalDistance(9).format shouldBe "9"
    }
  }
}
