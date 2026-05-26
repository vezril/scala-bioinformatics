package bio.algorithms.nucleic

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DnaReverseComplementSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"Invalid DNA in test: $s"))

  describe("DnaReverseComplement.reverseComplement") {
    it("produces the Rosalind sample reverse complement") {
      DnaReverseComplement.reverseComplement(dna("AAAACCCGGT")) shouldBe dna("ACCGGGTTTT")
    }

    it("returns empty for empty input") {
      DnaReverseComplement.reverseComplement(dna("")) shouldBe dna("")
    }

    it("complements a single A to T") {
      DnaReverseComplement.reverseComplement(dna("A")) shouldBe dna("T")
    }

    it("complements a single T to A") {
      DnaReverseComplement.reverseComplement(dna("T")) shouldBe dna("A")
    }

    it("complements a single C to G") {
      DnaReverseComplement.reverseComplement(dna("C")) shouldBe dna("G")
    }

    it("complements a single G to C") {
      DnaReverseComplement.reverseComplement(dna("G")) shouldBe dna("C")
    }

    it("returns the same value for a self-complementary palindrome (GGCC)") {
      DnaReverseComplement.reverseComplement(dna("GGCC")) shouldBe dna("GGCC")
    }

    it("complements an all-same-base string (AAAA -> TTTT)") {
      DnaReverseComplement.reverseComplement(dna("AAAA")) shouldBe dna("TTTT")
    }

    it("is its own inverse (applying twice returns the original)") {
      val input = dna("AAAACCCGGT")
      DnaReverseComplement.reverseComplement(DnaReverseComplement.reverseComplement(input)) shouldBe input
    }
  }
}
