package bio.algorithms.analysis

import bio.domain.analysis.ExpectedRestrictionSitesProblem
import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ExpectedRestrictionSitesAlgoSpec extends AnyFunSpec with Matchers {

  private def gcs(ds: Double*): Vector[Probability] =
    ds.iterator
      .map(d => Probability.from(d).getOrElse(sys.error(s"invalid Probability: $d")))
      .toVector

  private def problem(
      motif: String,
      length: Int,
      a: Vector[Probability]
  ): ExpectedRestrictionSitesProblem =
    ExpectedRestrictionSitesProblem
      .from(
        DnaString.from(motif).getOrElse(sys.error(s"invalid DnaString: $motif")),
        length,
        a
      )
      .getOrElse(sys.error("invalid ExpectedRestrictionSitesProblem fixture"))

  private def counts(motif: String, length: Int, a: Vector[Probability]): Vector[Double] =
    ExpectedRestrictionSites.expectedCounts(problem(motif, length, a)).expectations

  describe("ExpectedRestrictionSites.expectedCounts") {
    it("computes the canonical Rosalind EVAL sample within 0.001") {
      val result = counts("AG", 10, gcs(0.25, 0.5, 0.75))
      result should have size 3
      result(0) shouldBe 0.422 +- 0.001
      result(1) shouldBe 0.563 +- 0.001
      result(2) shouldBe 0.422 +- 0.001
    }

    it("yields zero when the motif is longer than the string") {
      counts("AG", 1, gcs(0.5)) shouldBe Vector(0.0)
    }

    it("scales the single-position probability by the position count") {
      counts("AT", 10, gcs(0.5)).head shouldBe 0.5625 +- 0.001
    }

    it("yields zero when a motif symbol is impossible under the GC-content") {
      counts("GG", 10, gcs(0.0)) shouldBe Vector(0.0)
    }

    it("yields an empty result for an empty GC-content array") {
      counts("AG", 10, Vector.empty) shouldBe empty
    }
  }
}
