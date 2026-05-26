package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DnaNucleotideSpec extends AnyFunSpec with Matchers {

  describe("DnaNucleotide") {
    it("has exactly four case objects: A, C, G, T") {
      val bases: Seq[DnaNucleotide] = Seq(DnaNucleotide.A, DnaNucleotide.C, DnaNucleotide.G, DnaNucleotide.T)
      bases should have size 4
    }

    it("supports exhaustive pattern matching without warnings") {
      def label(n: DnaNucleotide): String = n match {
        case DnaNucleotide.A => "adenine"
        case DnaNucleotide.C => "cytosine"
        case DnaNucleotide.G => "guanine"
        case DnaNucleotide.T => "thymine"
      }
      label(DnaNucleotide.T) shouldBe "thymine"
    }
  }

  describe("DnaNucleotide.validChars") {
    it("contains exactly A, C, G, T") {
      DnaNucleotide.validChars shouldBe Set('A', 'C', 'G', 'T')
    }

    it("does not contain RNA-specific U") {
      DnaNucleotide.validChars.contains('U') shouldBe false
    }
  }

  describe("DnaNucleotide.fromChar") {
    it("returns Some(A) for 'A'") {
      DnaNucleotide.fromChar('A') shouldBe Some(DnaNucleotide.A)
    }

    it("returns Some(C) for 'C'") {
      DnaNucleotide.fromChar('C') shouldBe Some(DnaNucleotide.C)
    }

    it("returns Some(G) for 'G'") {
      DnaNucleotide.fromChar('G') shouldBe Some(DnaNucleotide.G)
    }

    it("returns Some(T) for 'T'") {
      DnaNucleotide.fromChar('T') shouldBe Some(DnaNucleotide.T)
    }

    it("returns None for RNA-specific U") {
      DnaNucleotide.fromChar('U') shouldBe None
    }

    it("returns None for lowercase") {
      DnaNucleotide.fromChar('a') shouldBe None
    }
  }

  describe("DnaNucleotide.toChar") {
    it("returns 'A' for A") {
      DnaNucleotide.toChar(DnaNucleotide.A) shouldBe 'A'
    }

    it("returns 'C' for C") {
      DnaNucleotide.toChar(DnaNucleotide.C) shouldBe 'C'
    }

    it("returns 'G' for G") {
      DnaNucleotide.toChar(DnaNucleotide.G) shouldBe 'G'
    }

    it("returns 'T' for T") {
      DnaNucleotide.toChar(DnaNucleotide.T) shouldBe 'T'
    }

    it("is the inverse of fromChar for valid bases") {
      Set('A', 'C', 'G', 'T').foreach { c =>
        DnaNucleotide.fromChar(c).map(DnaNucleotide.toChar) shouldBe Some(c)
      }
    }
  }

  describe("DnaNucleotide.complement") {
    it("complements A to T") {
      DnaNucleotide.complement(DnaNucleotide.A) shouldBe DnaNucleotide.T
    }

    it("complements T to A") {
      DnaNucleotide.complement(DnaNucleotide.T) shouldBe DnaNucleotide.A
    }

    it("complements C to G") {
      DnaNucleotide.complement(DnaNucleotide.C) shouldBe DnaNucleotide.G
    }

    it("complements G to C") {
      DnaNucleotide.complement(DnaNucleotide.G) shouldBe DnaNucleotide.C
    }

    it("is its own inverse (applying twice returns the original)") {
      Seq(DnaNucleotide.A, DnaNucleotide.C, DnaNucleotide.G, DnaNucleotide.T).foreach { n =>
        DnaNucleotide.complement(DnaNucleotide.complement(n)) shouldBe n
      }
    }
  }
}
