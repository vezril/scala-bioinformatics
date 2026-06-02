package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProteinMassProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("ProteinMassProblemError") {
    it("constructs ProteinTooLong as a ProteinMassProblemError subtype") {
      val err: ProteinMassProblemError =
        ProteinMassProblemError.ProteinTooLong(1001, 1000)
      err shouldBe ProteinMassProblemError.ProteinTooLong(1001, 1000)
    }
  }
}
