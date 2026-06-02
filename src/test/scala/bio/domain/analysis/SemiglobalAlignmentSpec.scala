package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SemiglobalAlignmentSpec extends AnyFunSpec with Matchers {

  describe("SemiglobalAlignment.format") {
    it("renders score, augmented s, and augmented t on separate lines") {
      val result =
        SemiglobalAlignment(4, "CAGCA-CTTGGATTCTCGG", "---CAGCGTGG--------")
      result.format shouldBe "4\nCAGCA-CTTGGATTCTCGG\n---CAGCGTGG--------"
    }
  }
}
