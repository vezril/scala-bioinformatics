package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CyclicSuperstringSpec extends AnyFunSpec with Matchers {

  describe("CyclicSuperstring.format") {
    it("renders the bare symbol sequence with no surrounding punctuation") {
      CyclicSuperstring("GATTACA").format shouldBe "GATTACA"
    }

    it("renders a single-symbol chromosome") {
      CyclicSuperstring("A").format shouldBe "A"
    }
  }
}
