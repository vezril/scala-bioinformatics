package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RnaNucleotideCountsSpec extends AnyFunSpec with Matchers {

  describe("RnaNucleotideCounts") {
    it("holds a, c, g, u fields") {
      val counts = RnaNucleotideCounts(a = 5, c = 3, g = 7, u = 2)
      counts.a shouldBe 5
      counts.c shouldBe 3
      counts.g shouldBe 7
      counts.u shouldBe 2
    }

    it("formats as space-separated A C G U integers") {
      RnaNucleotideCounts(a = 5, c = 3, g = 7, u = 2).format shouldBe "5 3 7 2"
    }

    it("formats all-zero counts correctly") {
      RnaNucleotideCounts(a = 0, c = 0, g = 0, u = 0).format shouldBe "0 0 0 0"
    }
  }
}
