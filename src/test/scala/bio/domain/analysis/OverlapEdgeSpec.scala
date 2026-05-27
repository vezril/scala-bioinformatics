package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OverlapEdgeSpec extends AnyFunSpec with Matchers {

  describe("OverlapEdge") {
    it("exposes from and to fields") {
      val edge = OverlapEdge("Rosalind_0498", "Rosalind_2391")
      edge.from shouldBe "Rosalind_0498"
      edge.to shouldBe "Rosalind_2391"
    }
  }
}
