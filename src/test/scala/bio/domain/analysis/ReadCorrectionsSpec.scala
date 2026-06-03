package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReadCorrectionsSpec extends AnyFunSpec with Matchers {
  describe("Correction.format") {
    it("renders the substitution as old->new") {
      Correction("TTCAT", "TTGAT").format shouldBe "TTCAT->TTGAT"
    }
  }

  describe("ReadCorrections.format") {
    it("renders each correction on its own line") {
      ReadCorrections(
        Vector(Correction("TTCAT", "TTGAT"), Correction("GAGGA", "GATGA"))
      ).format shouldBe "TTCAT->TTGAT\nGAGGA->GATGA"
    }
  }
}
