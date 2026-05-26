package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RnaNucleotideSpec extends AnyFunSpec with Matchers {

  describe("RnaNucleotide") {
    it("has exactly four case objects: A, C, G, U") {
      val bases: Seq[RnaNucleotide] = Seq(RnaNucleotide.A, RnaNucleotide.C, RnaNucleotide.G, RnaNucleotide.U)
      bases should have size 4
    }

    it("supports exhaustive pattern matching without warnings") {
      def label(n: RnaNucleotide): String = n match {
        case RnaNucleotide.A => "adenine"
        case RnaNucleotide.C => "cytosine"
        case RnaNucleotide.G => "guanine"
        case RnaNucleotide.U => "uracil"
      }
      label(RnaNucleotide.U) shouldBe "uracil"
    }
  }

  describe("RnaNucleotide.validChars") {
    it("contains exactly A, C, G, U") {
      RnaNucleotide.validChars shouldBe Set('A', 'C', 'G', 'U')
    }

    it("does not contain DNA-specific T") {
      RnaNucleotide.validChars.contains('T') shouldBe false
    }
  }

  describe("RnaNucleotide.fromChar") {
    it("returns Some(A) for 'A'") {
      RnaNucleotide.fromChar('A') shouldBe Some(RnaNucleotide.A)
    }

    it("returns Some(C) for 'C'") {
      RnaNucleotide.fromChar('C') shouldBe Some(RnaNucleotide.C)
    }

    it("returns Some(G) for 'G'") {
      RnaNucleotide.fromChar('G') shouldBe Some(RnaNucleotide.G)
    }

    it("returns Some(U) for 'U'") {
      RnaNucleotide.fromChar('U') shouldBe Some(RnaNucleotide.U)
    }

    it("returns None for DNA-specific T") {
      RnaNucleotide.fromChar('T') shouldBe None
    }

    it("returns None for lowercase") {
      RnaNucleotide.fromChar('u') shouldBe None
    }
  }
}
