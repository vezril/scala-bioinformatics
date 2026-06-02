package bio.algorithms.graph

import bio.domain.graph.{LongestRepeatProblem, SuffixTreeEdge}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LongestMultipleRepeatSpec extends AnyFunSpec with Matchers {

  private val text = "CATACATAC$"

  /** The suffix tree of CATACATAC$ from the canonical Rosalind LREP sample. */
  private val edges = Vector(
    SuffixTreeEdge("node1", "node2", 1, 1),
    SuffixTreeEdge("node1", "node7", 2, 1),
    SuffixTreeEdge("node1", "node14", 3, 3),
    SuffixTreeEdge("node1", "node17", 10, 1),
    SuffixTreeEdge("node2", "node3", 2, 4),
    SuffixTreeEdge("node2", "node6", 10, 1),
    SuffixTreeEdge("node3", "node4", 6, 5),
    SuffixTreeEdge("node3", "node5", 10, 1),
    SuffixTreeEdge("node7", "node8", 3, 3),
    SuffixTreeEdge("node7", "node11", 5, 1),
    SuffixTreeEdge("node8", "node9", 6, 5),
    SuffixTreeEdge("node8", "node10", 10, 1),
    SuffixTreeEdge("node11", "node12", 6, 5),
    SuffixTreeEdge("node11", "node13", 10, 1),
    SuffixTreeEdge("node14", "node15", 6, 5),
    SuffixTreeEdge("node14", "node16", 10, 1)
  )

  private def repeat(k: Int): String =
    LongestMultipleRepeat
      .find(LongestRepeatProblem.from(text, k, edges).getOrElse(sys.error("bad fixture")))
      .substring

  describe("LongestMultipleRepeat.find") {
    it("finds the canonical Rosalind LREP sample (k = 2)") {
      repeat(2) shouldBe "CATAC"
    }

    it("returns the deepest substring meeting a higher threshold (k = 4)") {
      repeat(4) shouldBe "A"
    }

    it("returns the empty substring when no repeat meets the threshold (k = 5)") {
      repeat(5) shouldBe ""
    }
  }
}
