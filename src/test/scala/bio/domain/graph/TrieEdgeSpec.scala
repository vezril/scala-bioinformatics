package bio.domain.graph

import bio.domain.nucleic.DnaNucleotide
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TrieEdgeSpec extends AnyFunSpec with Matchers {

  describe("TrieEdge") {
    it("exposes parent, child, and symbol") {
      val edge = TrieEdge(1, 2, DnaNucleotide.A)
      edge.parent shouldBe 1
      edge.child shouldBe 2
      edge.symbol shouldBe DnaNucleotide.A
    }
  }
}
