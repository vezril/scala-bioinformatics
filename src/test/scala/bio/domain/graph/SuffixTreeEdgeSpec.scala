package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SuffixTreeEdgeSpec extends AnyFunSpec with Matchers {

  describe("SuffixTreeEdge") {
    it("exposes parent, child, start, and length") {
      val edge = SuffixTreeEdge("node1", "node2", 1, 1)
      edge.parent shouldBe "node1"
      edge.child shouldBe "node2"
      edge.start shouldBe 1
      edge.length shouldBe 1
    }
  }
}
