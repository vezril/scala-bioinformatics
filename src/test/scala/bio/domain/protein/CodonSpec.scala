package bio.domain.protein

import bio.domain.nucleic.{RnaNucleotide, RnaString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CodonSpec extends AnyFunSpec with Matchers {

  import RnaNucleotide.{A, C, G, U}

  private def rna(s: String): RnaString =
    RnaString.from(s).getOrElse(sys.error(s"invalid RNA in test fixture: $s"))

  describe("Codon") {
    it("exposes its three nucleotides") {
      val codon = Codon(A, U, G)
      codon.first  shouldBe A
      codon.second shouldBe U
      codon.third  shouldBe G
    }
  }

  describe("Codon.fromChars") {
    it("returns Some(codon) for three valid RNA chars") {
      Codon.fromChars('A', 'U', 'G') shouldBe Some(Codon(A, U, G))
    }

    it("returns None when any char is not valid RNA") {
      Codon.fromChars('A', 'T', 'G') shouldBe None
      Codon.fromChars('X', 'U', 'G') shouldBe None
      Codon.fromChars('A', 'U', 'Z') shouldBe None
    }
  }

  describe("Codon.parseAll") {
    it("splits a length-6 RnaString into two ordered codons") {
      Codon.parseAll(rna("AUGCCC")) shouldBe Right(Vector(Codon(A, U, G), Codon(C, C, C)))
    }

    it("returns a singleton vector for a length-3 RnaString") {
      Codon.parseAll(rna("UAA")) shouldBe Right(Vector(Codon(U, A, A)))
    }

    it("returns Vector.empty for an empty RnaString") {
      Codon.parseAll(rna("")) shouldBe Right(Vector.empty)
    }

    it("rejects a length-5 RnaString as LengthNotMultipleOfThree(5)") {
      Codon.parseAll(rna("AUGCC")) shouldBe
        Left(TranslationError.LengthNotMultipleOfThree(5))
    }

    it("rejects a length-1 RnaString as LengthNotMultipleOfThree(1)") {
      Codon.parseAll(rna("A")) shouldBe
        Left(TranslationError.LengthNotMultipleOfThree(1))
    }
  }
}
