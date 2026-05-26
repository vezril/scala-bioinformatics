package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DnaNucleotideCountsSpec extends AnyFunSpec with Matchers {

  describe("DnaNucleotideCounts") {
    it("holds a, c, g, t fields") {
      val counts = DnaNucleotideCounts(a = 1, c = 2, g = 3, t = 4)
      counts.a shouldBe 1
      counts.c shouldBe 2
      counts.g shouldBe 3
      counts.t shouldBe 4
    }

    it("formats as space-separated A C G T integers") {
      DnaNucleotideCounts(a = 20, c = 12, g = 17, t = 21).format shouldBe "20 12 17 21"
    }

    it("formats all-zero counts correctly") {
      DnaNucleotideCounts(a = 0, c = 0, g = 0, t = 0).format shouldBe "0 0 0 0"
    }

    it("formats single-nucleotide counts correctly") {
      DnaNucleotideCounts(a = 1, c = 0, g = 0, t = 0).format shouldBe "1 0 0 0"
    }
  }
}
