package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MonotonicSubsequencesSpec extends AnyFunSpec with Matchers {
  describe("MonotonicSubsequences.format") {
    it("renders the two subsequences on separate lines") {
      MonotonicSubsequences(Vector(1, 2, 3), Vector(5, 4, 2)).format shouldBe "1 2 3\n5 4 2"
    }
  }
}
