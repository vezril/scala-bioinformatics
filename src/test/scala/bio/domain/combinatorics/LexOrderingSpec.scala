package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LexOrderingSpec extends AnyFunSpec with Matchers {
  describe("LexOrdering.format") {
    it("renders the strings one per line") {
      LexOrdering(Vector("D", "DD", "DN")).format shouldBe "D\nDD\nDN"
    }
  }
}
