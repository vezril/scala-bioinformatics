package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UndirectedEdgeSpec extends AnyFunSpec with Matchers {

  describe("UndirectedEdge.from") {
    it("accepts a well-formed edge (1, 2)") {
      val edge = UndirectedEdge.from(1, 2).toOption.get
      edge.u shouldBe 1
      edge.v shouldBe 2
    }

    it("rejects u = 0 as NonPositiveU(0)") {
      UndirectedEdge.from(0, 5) shouldBe Left(UndirectedEdgeError.NonPositiveU(0))
    }

    it("rejects negative v as NonPositiveV") {
      UndirectedEdge.from(5, -3) shouldBe Left(UndirectedEdgeError.NonPositiveV(-3))
    }

    it("rejects a self-loop (7, 7) as SelfLoop(7)") {
      UndirectedEdge.from(7, 7) shouldBe Left(UndirectedEdgeError.SelfLoop(7))
    }

    it("validates u lower bound before v lower bound") {
      UndirectedEdge.from(0, -1) shouldBe Left(UndirectedEdgeError.NonPositiveU(0))
    }

    it("validates v lower bound before self-loop") {
      UndirectedEdge.from(7, 0) shouldBe Left(UndirectedEdgeError.NonPositiveV(0))
    }
  }

  describe("UndirectedEdge construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.graph.UndirectedEdge(1, 2)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.UndirectedEdge.from(1, 2).toOption.get.copy(u = 99)"""
      )
    }
  }
}
