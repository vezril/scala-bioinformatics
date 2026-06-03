package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversalSortingSpec extends AnyFunSpec with Matchers {
  describe("ReversalSorting.format") {
    it("renders the distance and the reversals on separate lines") {
      ReversalSorting(2, Vector(Reversal(4, 9), Reversal(2, 5))).format shouldBe "2\n4 9\n2 5"
    }

    it("renders a zero-distance sorting as just the count") {
      ReversalSorting(0, Vector.empty).format shouldBe "0"
    }
  }
}
