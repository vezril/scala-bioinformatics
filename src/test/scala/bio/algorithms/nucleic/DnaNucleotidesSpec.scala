package bio.algorithms.nucleic

import bio.domain.nucleic.{DnaString, DnaNucleotideCounts}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DnaNucleotidesSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"Invalid DNA in test: $s"))

  describe("DnaNucleotides.count") {
    it("counts the Rosalind sample correctly") {
      val input = dna("AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC")
      DnaNucleotides.count(input) shouldBe DnaNucleotideCounts(a = 20, c = 12, g = 17, t = 21)
    }

    it("counts a single A nucleotide") {
      DnaNucleotides.count(dna("A")) shouldBe DnaNucleotideCounts(a = 1, c = 0, g = 0, t = 0)
    }

    it("returns all zeros for an empty string") {
      DnaNucleotides.count(dna("")) shouldBe DnaNucleotideCounts(a = 0, c = 0, g = 0, t = 0)
    }

    it("counts a string with only T nucleotides") {
      DnaNucleotides.count(dna("TTTTTT")) shouldBe DnaNucleotideCounts(a = 0, c = 0, g = 0, t = 6)
    }
  }
}
