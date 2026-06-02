package bio.domain.analysis

import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ExpectedRestrictionSitesProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private def gcs(ds: Double*): Vector[Probability] =
    ds.iterator
      .map(d => Probability.from(d).getOrElse(sys.error(s"invalid Probability: $d")))
      .toVector

  describe("ExpectedRestrictionSitesProblem.from") {
    it("accepts inputs within bounds, preserving them") {
      val motif = dna("AG")
      val a = gcs(0.25, 0.5, 0.75)
      val result = ExpectedRestrictionSitesProblem.from(motif, 10, a)
      result.map(_.motif) shouldBe Right(motif)
      result.map(_.length) shouldBe Right(10)
      result.map(_.gcContents) shouldBe Right(a)
    }

    it("accepts the upper bounds (10 bp motif, length 1000000, 20 GC-contents)") {
      val a = gcs(List.fill(20)(0.5): _*)
      ExpectedRestrictionSitesProblem.from(dna("A" * 10), 1000000, a).isRight shouldBe true
    }

    it("accepts an empty motif") {
      ExpectedRestrictionSitesProblem.from(dna(""), 10, gcs(0.5)).isRight shouldBe true
    }

    it("rejects a motif longer than 10 bp") {
      ExpectedRestrictionSitesProblem.from(dna("A" * 12), 10, gcs(0.5)) shouldBe Left(
        ExpectedRestrictionSitesProblemError.MotifTooLong(12, 10)
      )
    }

    it("rejects an odd-length motif") {
      ExpectedRestrictionSitesProblem.from(dna("AGT"), 10, gcs(0.5)) shouldBe Left(
        ExpectedRestrictionSitesProblemError.OddMotifLength(3)
      )
    }

    it("rejects a non-positive length") {
      ExpectedRestrictionSitesProblem.from(dna("AG"), 0, gcs(0.5)) shouldBe Left(
        ExpectedRestrictionSitesProblemError.NonPositiveLength(0)
      )
    }

    it("rejects a length above the maximum") {
      ExpectedRestrictionSitesProblem.from(dna("AG"), 1000001, gcs(0.5)) shouldBe Left(
        ExpectedRestrictionSitesProblemError.LengthTooLarge(1000001, 1000000)
      )
    }

    it("rejects too many GC-contents") {
      val a = gcs(List.fill(21)(0.5): _*)
      ExpectedRestrictionSitesProblem.from(dna("AG"), 10, a) shouldBe Left(
        ExpectedRestrictionSitesProblemError.TooManyGcContents(21, 20)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.ExpectedRestrictionSitesProblem(bio.domain.nucleic.DnaString.from("AG").toOption.get, 10, Vector.empty[bio.domain.stats.Probability])"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.ExpectedRestrictionSitesProblem.from(bio.domain.nucleic.DnaString.from("AG").toOption.get, 10, Vector.empty[bio.domain.stats.Probability]).toOption.get.copy()"""
      )
    }
  }
}
