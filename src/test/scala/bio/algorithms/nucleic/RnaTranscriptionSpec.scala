package bio.algorithms.nucleic

import bio.domain.nucleic.{DnaString, RnaString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RnaTranscriptionSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"Invalid DNA in test: $s"))

  private def rna(s: String): RnaString =
    RnaString.from(s).getOrElse(sys.error(s"Invalid RNA in test: $s"))

  describe("RnaTranscription.transcribe") {
    it("transcribes the Rosalind sample correctly") {
      RnaTranscription.transcribe(dna("GATGGAACTTGACTACGTAAATT")) shouldBe rna("GAUGGAACUUGACUACGUAAAUU")
    }

    it("replaces every T with U") {
      RnaTranscription.transcribe(dna("TTTT")) shouldBe rna("UUUU")
    }

    it("preserves A, C, G bases unchanged") {
      RnaTranscription.transcribe(dna("ACGACG")) shouldBe rna("ACGACG")
    }

    it("returns empty RnaString for empty DnaString") {
      RnaTranscription.transcribe(dna("")) shouldBe rna("")
    }

    it("transcribes mixed bases correctly") {
      RnaTranscription.transcribe(dna("ATCG")) shouldBe rna("AUCG")
    }

    it("transcribes a single T to U") {
      RnaTranscription.transcribe(dna("T")) shouldBe rna("U")
    }

    it("preserves a single A") {
      RnaTranscription.transcribe(dna("A")) shouldBe rna("A")
    }
  }
}
