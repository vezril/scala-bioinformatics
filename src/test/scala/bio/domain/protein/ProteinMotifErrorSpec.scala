package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProteinMotifErrorSpec extends AnyFunSpec with Matchers {
  describe("ProteinMotifError.UnexpectedCharacter") {
    it("carries the offending character and its index") {
      val err = ProteinMotifError.UnexpectedCharacter('}', 3)
      err.char shouldBe '}'
      err.index shouldBe 3
    }
  }
}
