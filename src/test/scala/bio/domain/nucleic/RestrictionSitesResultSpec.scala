package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RestrictionSitesResultSpec extends AnyFunSpec with Matchers {

  private val SampleSites = Vector(
    RestrictionSite(4, 6),
    RestrictionSite(5, 4),
    RestrictionSite(6, 6),
    RestrictionSite(7, 4),
    RestrictionSite(17, 4),
    RestrictionSite(18, 4),
    RestrictionSite(20, 6),
    RestrictionSite(21, 4)
  )

  describe("RestrictionSite") {
    it("exposes its 1-based position and length") {
      val site = RestrictionSite(4, 6)
      site.position shouldBe 4
      site.length shouldBe 6
    }
  }

  describe("RestrictionSites.format") {
    it("renders the canonical sample as position/length lines joined by newlines") {
      RestrictionSites(SampleSites).format shouldBe
        "4 6\n5 4\n6 6\n7 4\n17 4\n18 4\n20 6\n21 4"
    }

    it("renders an empty result as the empty string") {
      RestrictionSites(Vector.empty).format shouldBe ""
    }
  }
}
