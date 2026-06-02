package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SuffixTreeEncodingResultSpec extends AnyFunSpec with Matchers {

  describe("SuffixTreeEncoding result") {
    it("exposes the edge labels") {
      val edges = Vector("A$", "$")
      SuffixTreeEncoding(edges).edges shouldBe edges
    }

    it("formats one label per line") {
      SuffixTreeEncoding(Vector("A$", "$")).format shouldBe "A$\n$"
    }

    it("renders the empty result as the empty string") {
      SuffixTreeEncoding(Vector.empty).format shouldBe ""
    }
  }
}
