package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OverlapAlignmentSpec extends AnyFunSpec with Matchers {

  describe("OverlapAlignment.format") {
    it("renders score, augmented s, and augmented t on separate lines") {
      val result = OverlapAlignment(1, "ATTAGAC-AG", "AT-AGACCAT")
      result.format shouldBe "1\nATTAGAC-AG\nAT-AGACCAT"
    }
  }
}
