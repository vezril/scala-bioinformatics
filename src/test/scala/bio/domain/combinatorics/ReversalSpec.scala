package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversalSpec extends AnyFunSpec with Matchers {
  describe("Reversal.format") {
    it("renders the endpoints space-separated") {
      Reversal(4, 9).format shouldBe "4 9"
    }
  }
}
