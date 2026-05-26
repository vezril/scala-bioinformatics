package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GcContentSpec extends AnyFunSpec with Matchers {

  private val Tolerance: Double = 1e-9

  describe("GcContent.from") {
    it("accepts 50.0") {
      GcContent.from(50.0).map(_.value) shouldBe Right(50.0)
    }

    it("accepts the lower bound 0.0") {
      GcContent.from(0.0).map(_.value) shouldBe Right(0.0)
    }

    it("accepts the upper bound 100.0") {
      GcContent.from(100.0).map(_.value) shouldBe Right(100.0)
    }

    it("rejects a value greater than 100") {
      GcContent.from(100.1) shouldBe Left(GcContentError.OutOfRange(100.1))
    }

    it("rejects a negative value") {
      GcContent.from(-0.1) shouldBe Left(GcContentError.OutOfRange(-0.1))
    }

    it("rejects NaN") {
      GcContent.from(Double.NaN) shouldBe Left(GcContentError.NotFinite)
    }

    it("rejects positive infinity") {
      GcContent.from(Double.PositiveInfinity) shouldBe Left(GcContentError.NotFinite)
    }
  }

  describe("GcContent construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.analysis.GcContent(50.0)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile("""bio.domain.analysis.GcContent.from(0).toOption.get.copy(value = 99.0)""")
    }
  }

  describe("GcContent.of") {
    it("computes 37.5% for the documented example AGCTATAG") {
      val dna = DnaString.from("AGCTATAG").getOrElse(sys.error("invalid"))
      GcContent.of(dna).value shouldBe 37.5 +- Tolerance
    }

    it("computes 100.0% for an all-GC sequence") {
      val dna = DnaString.from("GCGC").getOrElse(sys.error("invalid"))
      GcContent.of(dna).value shouldBe 100.0 +- Tolerance
    }

    it("computes 0.0% for an all-AT sequence") {
      val dna = DnaString.from("ATAT").getOrElse(sys.error("invalid"))
      GcContent.of(dna).value shouldBe 0.0 +- Tolerance
    }

    it("returns 0.0% for an empty DnaString by convention") {
      val dna = DnaString.from("").getOrElse(sys.error("invalid"))
      GcContent.of(dna).value shouldBe 0.0
    }

    it("computes 100.0% for a single G") {
      val dna = DnaString.from("G").getOrElse(sys.error("invalid"))
      GcContent.of(dna).value shouldBe 100.0 +- Tolerance
    }

    it("computes 0.0% for a single A") {
      val dna = DnaString.from("A").getOrElse(sys.error("invalid"))
      GcContent.of(dna).value shouldBe 0.0 +- Tolerance
    }
  }
}
