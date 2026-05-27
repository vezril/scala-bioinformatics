package bio.algorithms.protein

import bio.domain.nucleic.DnaString
import bio.domain.protein.{RnaSplicingProblem, TranslationError}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RnaSplicingSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString in fixture: $s"))

  private def problem(source: String, introns: Vector[String]): RnaSplicingProblem =
    RnaSplicingProblem
      .from(dna(source), introns.map(dna))
      .getOrElse(sys.error("invalid RnaSplicingProblem in fixture"))

  describe("RnaSplicing.transcribeAndTranslate") {
    it("produces MVYIADKQHVASREAYGHMFKVCA for the canonical Rosalind sample") {
      val p = problem(
        "ATGGTCTACATAGCTGACAAACAGCACGTAGCAATCGGTCGAATCTCGAGAGGCATATGGTCACATGATCGGTCGAGCGTGTTTCAAAGTTTGCGCCTAG",
        Vector("ATCGGTCGAA", "ATCGGTCGAGCGTGT")
      )
      RnaSplicing.transcribeAndTranslate(p).map(_.value) shouldBe Right("MVYIADKQHVASREAYGHMFKVCA")
    }

    it("matches direct translation when no introns are supplied (spec-8 sample DNA)") {
      // spec-8 RNA sample "AUGGCCAUGGCGCCCAGAACUGAGAUCAAUAGUACCCGUAUUAACGGGUGA" -> "MAMAPRTEINSTRING"
      // Equivalent DNA: replace U with T
      val p = problem(
        "ATGGCCATGGCGCCCAGAACTGAGATCAATAGTACCCGTATTAACGGGTGA",
        Vector.empty
      )
      RnaSplicing.transcribeAndTranslate(p).map(_.value) shouldBe Right("MAMAPRTEINSTRING")
    }

    it("returns an empty protein when an intron equals the entire source") {
      val p = problem("ATGTAA", Vector("ATGTAA"))
      RnaSplicing.transcribeAndTranslate(p).map(_.value) shouldBe Right("")
    }

    it("propagates TranslationError when the spliced RNA length is not a multiple of 3") {
      // Source length 4 (not a multiple of 3), no introns: transcribes to "AAAA" -> Left(LengthNotMultipleOfThree(4))
      val p = problem("AAAA", Vector.empty)
      RnaSplicing.transcribeAndTranslate(p) shouldBe
        Left(TranslationError.LengthNotMultipleOfThree(4))
    }
  }
}
