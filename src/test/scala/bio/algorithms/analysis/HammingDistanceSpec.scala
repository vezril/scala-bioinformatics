package bio.algorithms.analysis

import bio.domain.analysis.HammingError
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class HammingDistanceSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DNA in test fixture: $s"))

  describe("HammingDistance.between") {
    it("produces 7 for the Rosalind sample") {
      val a = dna("GAGCCTACTAACGGGAT")
      val b = dna("CATCGTAATGACGGCCT")
      HammingDistance.between(a, b) shouldBe Right(7)
    }

    it("returns 0 for identical strings") {
      HammingDistance.between(dna("ACGT"), dna("ACGT")) shouldBe Right(0)
    }

    it("returns 0 for two empty DnaStrings") {
      HammingDistance.between(dna(""), dna("")) shouldBe Right(0)
    }

    it("returns the length for all-different equal-length sequences") {
      HammingDistance.between(dna("AAAA"), dna("TTTT")) shouldBe Right(4)
    }

    it("returns 1 for a single differing position") {
      HammingDistance.between(dna("ACGT"), dna("ACGA")) shouldBe Right(1)
    }

    it("is symmetric: between(a, b) == between(b, a)") {
      val a = dna("GAGCCTACTAACGGGAT")
      val b = dna("CATCGTAATGACGGCCT")
      HammingDistance.between(a, b) shouldBe HammingDistance.between(b, a)
    }

    it("rejects non-empty length-mismatched inputs") {
      HammingDistance.between(dna("ACGT"), dna("ACGTAA")) shouldBe
        Left(HammingError.LengthMismatch(4, 6))
    }

    it("rejects empty + non-empty as a length mismatch") {
      HammingDistance.between(dna(""), dna("AC")) shouldBe
        Left(HammingError.LengthMismatch(0, 2))
    }

    it("rejects non-empty + empty preserving argument order in the error") {
      HammingDistance.between(dna("AC"), dna("")) shouldBe
        Left(HammingError.LengthMismatch(2, 0))
    }
  }
}
