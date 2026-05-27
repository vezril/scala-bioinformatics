package bio.algorithms.nucleic

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class IntronSplicingSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString in fixture: $s"))

  describe("IntronSplicing.splice") {
    it("removes both introns from the Rosalind sample (exon extraction)") {
      val source = dna(
        "ATGGTCTACATAGCTGACAAACAGCACGTAGCAATCGGTCGAATCTCGAGAGGCATATGGTCACATGATCGGTCGAGCGTGTTTCAAAGTTTGCGCCTAG"
      )
      val introns = Vector(dna("ATCGGTCGAA"), dna("ATCGGTCGAGCGTGT"))
      val expected =
        "ATGGTCTACATAGCTGACAAACAGCACGTAGCATCTCGAGAGGCATATGGTCACATGTTCAAAGTTTGCGCCTAG"
      IntronSplicing.splice(source, introns).value shouldBe expected
    }

    it("returns the source unchanged when introns is empty") {
      IntronSplicing.splice(dna("ACGTACGT"), Vector.empty).value shouldBe "ACGTACGT"
    }

    it("returns the source unchanged when no intron appears in it") {
      IntronSplicing.splice(dna("AAAA"), Vector(dna("GGGG"))).value shouldBe "AAAA"
    }

    it("returns empty when the intron equals the entire source") {
      IntronSplicing.splice(dna("ACGT"), Vector(dna("ACGT"))).value shouldBe ""
    }

    it("removes every disjoint occurrence of the same intron") {
      IntronSplicing.splice(dna("AAACGTAAACGTAAA"), Vector(dna("CGT"))).value shouldBe "AAAAAAAAA"
    }

    it("applies introns in input order") {
      val result = IntronSplicing.splice(dna("ATCG"), Vector(dna("AT"), dna("CG"))).value
      result shouldBe "" // first "AT" removed -> "CG", then "CG" removed -> ""
    }
  }
}
