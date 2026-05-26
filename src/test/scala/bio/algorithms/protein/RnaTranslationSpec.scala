package bio.algorithms.protein

import bio.domain.nucleic.RnaString
import bio.domain.protein.{ProteinString, TranslationError}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RnaTranslationSpec extends AnyFunSpec with Matchers {

  private def rna(s: String): RnaString =
    RnaString.from(s).getOrElse(sys.error(s"invalid RNA in test fixture: $s"))

  describe("RnaTranslation.translate") {
    it("produces MAMAPRTEINSTRING for the Rosalind sample") {
      val input  = rna("AUGGCCAUGGCGCCCAGAACUGAGAUCAAUAGUACCCGUAUUAACGGGUGA")
      val result = RnaTranslation.translate(input)
      result.map(_.value) shouldBe Right("MAMAPRTEINSTRING")
    }

    it("produces an empty protein for empty RNA") {
      RnaTranslation.translate(rna("")).map(_.value) shouldBe Right("")
    }

    it("produces an empty protein when the RNA is a single Stop codon") {
      RnaTranslation.translate(rna("UAA")).map(_.value) shouldBe Right("")
    }

    it("halts at the first Stop codon, ignoring codons after it") {
      // AUG (M) UAA (Stop) GCC (would be A, but ignored)
      RnaTranslation.translate(rna("AUGUAAGCC")).map(_.value) shouldBe Right("M")
    }

    it("translates to the end when no Stop codon is present") {
      // AUG (M) GCC (A)
      RnaTranslation.translate(rna("AUGGCC")).map(_.value) shouldBe Right("MA")
    }

    it("rejects RNA whose length is not a multiple of 3") {
      RnaTranslation.translate(rna("AUGGC")) shouldBe
        Left(TranslationError.LengthNotMultipleOfThree(5))
    }

    it("rejects a length-1 RNA") {
      RnaTranslation.translate(rna("A")) shouldBe
        Left(TranslationError.LengthNotMultipleOfThree(1))
    }

    it("treats UAG and UGA as Stop codons too") {
      RnaTranslation.translate(rna("AUGUAGGCC")).map(_.value) shouldBe Right("M")
      RnaTranslation.translate(rna("AUGUGAGCC")).map(_.value) shouldBe Right("M")
    }

    it("returns a ProteinString whose validation is implicit") {
      // Sanity: the returned protein survives ProteinString.from round-trip
      val proteinValue = RnaTranslation
        .translate(rna("AUGGCC"))
        .getOrElse(sys.error("translation failed"))
        .value
      ProteinString.from(proteinValue).map(_.value) shouldBe Right(proteinValue)
    }
  }
}
