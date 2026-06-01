package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ShortestSuperstringSpec extends AnyFunSpec with Matchers {

  describe("ShortestSuperstring.format") {
    it("returns the assembled sequence") {
      ShortestSuperstring("ATTAGACCTGCCGGAATAC").format shouldBe "ATTAGACCTGCCGGAATAC"
    }

    it("returns a single read unchanged") {
      ShortestSuperstring("ACGT").format shouldBe "ACGT"
    }
  }
}
