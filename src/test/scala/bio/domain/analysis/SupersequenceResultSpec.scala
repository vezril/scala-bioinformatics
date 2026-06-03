package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SupersequenceResultSpec extends AnyFunSpec with Matchers {

  describe("Supersequence result") {
    it("exposes the supersequence") {
      Supersequence("ATGCATGAT").value shouldBe "ATGCATGAT"
    }

    it("formats the supersequence verbatim") {
      Supersequence("ATGCATGAT").format shouldBe "ATGCATGAT"
    }
  }
}
