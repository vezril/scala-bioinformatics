package bio.algorithms.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MotifLocationsSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DNA in test fixture: $s"))

  describe("MotifLocations.find") {
    it("produces 2 4 10 for the Rosalind sample") {
      val text  = dna("GATATATGCATATACTT")
      val motif = dna("ATAT")
      MotifLocations.find(text, motif) shouldBe Vector(2, 4, 10)
    }

    it("includes position 1 when the motif matches at the start of text") {
      val text  = dna("ACGTACGT")
      val motif = dna("AC")
      val result = MotifLocations.find(text, motif)
      result.headOption shouldBe Some(1)
      result          shouldBe Vector(1, 5)
    }

    it("includes the last possible start position when the motif matches at the end of text") {
      MotifLocations.find(dna("ACGT"), dna("GT")) shouldBe Vector(3)
    }

    it("includes overlapping matches") {
      MotifLocations.find(dna("AAAA"), dna("AA")) shouldBe Vector(1, 2, 3)
    }

    it("returns Vector(1) when text equals motif") {
      MotifLocations.find(dna("ACGT"), dna("ACGT")) shouldBe Vector(1)
    }

    it("returns Vector.empty when the motif does not occur") {
      MotifLocations.find(dna("AAAA"), dna("GG")) shouldBe Vector.empty
    }

    it("returns Vector.empty when the motif is longer than text") {
      MotifLocations.find(dna("AC"), dna("ACGT")) shouldBe Vector.empty
    }

    it("returns Vector.empty when text is empty and motif is non-empty") {
      MotifLocations.find(dna(""), dna("AC")) shouldBe Vector.empty
    }

    it("returns Vector.empty when motif is empty (by convention)") {
      MotifLocations.find(dna("ACGT"), dna("")) shouldBe Vector.empty
    }

    it("returns Vector.empty when both text and motif are empty") {
      MotifLocations.find(dna(""), dna("")) shouldBe Vector.empty
    }
  }
}
