package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InferredPeptideResultSpec extends AnyFunSpec with Matchers {

  describe("InferredPeptide result") {
    it("exposes the peptide") {
      InferredPeptide("KEKEP").peptide shouldBe "KEKEP"
    }

    it("formats the peptide verbatim") {
      InferredPeptide("KEKEP").format shouldBe "KEKEP"
    }
  }
}
