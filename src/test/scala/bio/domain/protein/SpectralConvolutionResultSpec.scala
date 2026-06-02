package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SpectralConvolutionResultSpec extends AnyFunSpec with Matchers {

  describe("SpectralConvolution.format") {
    it("renders the canonical sample result as multiplicity then shift") {
      SpectralConvolution(3, 85.03163).format shouldBe "3\n85.03163"
    }

    it("renders the absolute value of a negative shift") {
      SpectralConvolution(3, -85.03163).format shouldBe "3\n85.03163"
    }
  }
}
