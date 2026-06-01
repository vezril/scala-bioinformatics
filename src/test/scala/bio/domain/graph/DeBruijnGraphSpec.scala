package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DeBruijnGraphSpec extends AnyFunSpec with Matchers {

  describe("DeBruijnGraph.format") {
    it("renders each edge as (from, to), one per line, in order") {
      val graph = DeBruijnGraph(
        Vector(
          DeBruijnEdge("ATC", "TCA"),
          DeBruijnEdge("ATG", "TGA"),
          DeBruijnEdge("CAT", "ATC")
        )
      )
      graph.format shouldBe "(ATC, TCA)\n(ATG, TGA)\n(CAT, ATC)"
    }

    it("renders a single edge with no trailing newline") {
      DeBruijnGraph(Vector(DeBruijnEdge("ATA", "TAT"))).format shouldBe "(ATA, TAT)"
    }
  }
}
