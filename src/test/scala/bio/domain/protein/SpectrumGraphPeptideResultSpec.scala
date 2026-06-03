package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SpectrumGraphPeptideResultSpec extends AnyFunSpec with Matchers {

  describe("SpectrumGraphPeptide result") {
    it("exposes the peptide") {
      SpectrumGraphPeptide("WMSPG").peptide shouldBe "WMSPG"
    }

    it("formats the peptide verbatim") {
      SpectrumGraphPeptide("WMSPG").format shouldBe "WMSPG"
    }
  }
}
