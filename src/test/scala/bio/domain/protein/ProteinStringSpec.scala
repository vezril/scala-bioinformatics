package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProteinStringSpec extends AnyFunSpec with Matchers {

  describe("ProteinString.from") {
    it("accepts a string of valid single-letter codes") {
      ProteinString.from("MAMA").map(_.value) shouldBe Right("MAMA")
    }

    it("accepts an empty string") {
      ProteinString.from("").map(_.value) shouldBe Right("")
    }

    it("rejects a string containing a non-amino-acid letter") {
      ProteinString.from("MAB") shouldBe Left(ProteinError.InvalidCharacter('B'))
    }

    it("rejects lowercase letters") {
      ProteinString.from("mama") shouldBe Left(ProteinError.InvalidCharacter('m'))
    }

    it("rejects a string with whitespace") {
      ProteinString.from("MA MA") shouldBe Left(ProteinError.InvalidCharacter(' '))
    }
  }

  describe("ProteinString.fromAminoAcids") {
    it("assembles a protein from a vector of typed amino acids") {
      val aas = Vector(AminoAcid.M, AminoAcid.A, AminoAcid.M, AminoAcid.A)
      ProteinString.fromAminoAcids(aas).value shouldBe "MAMA"
    }

    it("handles an empty input") {
      ProteinString.fromAminoAcids(Vector.empty).value shouldBe ""
    }

    it("preserves order") {
      val aas = Vector(AminoAcid.W, AminoAcid.M, AminoAcid.A)
      ProteinString.fromAminoAcids(aas).value shouldBe "WMA"
    }
  }

  describe("ProteinString construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.protein.ProteinString("MAMA")""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile("""bio.domain.protein.ProteinString.from("M").toOption.get.copy(value = "X")""")
    }
  }
}
