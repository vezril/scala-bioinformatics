package bio.algorithms.nucleic

import bio.domain.nucleic.{DnaString, RestrictionSite, RestrictionSiteProblem}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RestrictionSitesAlgoSpec extends AnyFunSpec with Matchers {

  private def problem(s: String): RestrictionSiteProblem =
    RestrictionSiteProblem
      .from(DnaString.from(s).getOrElse(sys.error(s"invalid DnaString: $s")))
      .getOrElse(sys.error(s"invalid RestrictionSiteProblem fixture: $s"))

  describe("RestrictionSites.locate") {
    it("finds all sites in the canonical Rosalind REVP sample, in order") {
      RestrictionSites.locate(problem("TCAATGCATGCGGGTCTATATGCAT")).sites shouldBe
        Vector(
          RestrictionSite(4, 6),
          RestrictionSite(5, 4),
          RestrictionSite(6, 6),
          RestrictionSite(7, 4),
          RestrictionSite(17, 4),
          RestrictionSite(18, 4),
          RestrictionSite(20, 6),
          RestrictionSite(21, 4)
        )
    }

    it("returns no sites when none exist") {
      RestrictionSites.locate(problem("AAAAAAAA")).sites shouldBe empty
    }

    it("finds a single minimal length-4 site") {
      RestrictionSites.locate(problem("GTAC")).sites shouldBe
        Vector(RestrictionSite(1, 4))
    }

    it("ignores reverse palindromes shorter than 4") {
      RestrictionSites.locate(problem("GC")).sites shouldBe empty
    }

    it("returns only even lengths between 4 and 12") {
      RestrictionSites
        .locate(problem("TCAATGCATGCGGGTCTATATGCAT"))
        .sites
        .map(_.length)
        .foreach(len => Set(4, 6, 8, 10, 12) should contain(len))
    }
  }
}
