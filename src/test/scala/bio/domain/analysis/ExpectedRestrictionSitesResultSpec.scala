package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ExpectedRestrictionSitesResultSpec extends AnyFunSpec with Matchers {

  describe("ExpectedRestrictionSites result") {
    it("exposes the expected counts") {
      val counts = Vector(0.421875, 0.5625)
      ExpectedRestrictionSites(counts).expectations shouldBe counts
    }

    it("formats counts space-separated to three decimals") {
      ExpectedRestrictionSites(Vector(0.421875, 0.5625)).format shouldBe "0.422 0.563"
    }

    it("renders the empty result as the empty string") {
      ExpectedRestrictionSites(Vector.empty).format shouldBe ""
    }
  }
}
