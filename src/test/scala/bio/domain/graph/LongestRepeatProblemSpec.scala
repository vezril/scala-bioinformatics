package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LongestRepeatProblemSpec extends AnyFunSpec with Matchers {

  private val text = "CATACATAC$" // length 10
  private val edges = Vector(
    SuffixTreeEdge("node1", "node2", 1, 1),
    SuffixTreeEdge("node2", "node3", 2, 4)
  )

  describe("LongestRepeatProblem.from") {
    it("accepts a valid suffix tree problem, preserving the inputs") {
      val result = LongestRepeatProblem.from(text, 2, edges)
      result.map(_.text) shouldBe Right(text)
      result.map(_.k) shouldBe Right(2)
      result.map(_.edges) shouldBe Right(edges)
    }

    it("accepts an empty edge list") {
      LongestRepeatProblem.from(text, 2, Vector.empty).isRight shouldBe true
    }

    it("rejects a non-positive k") {
      LongestRepeatProblem.from(text, 0, edges) shouldBe Left(
        LongestRepeatProblemError.NonPositiveK(0)
      )
    }

    it("rejects a text longer than the maximum") {
      val tooLong = "A" * 20001 + "$" // length 20002
      LongestRepeatProblem.from(tooLong, 2, Vector.empty) shouldBe Left(
        LongestRepeatProblemError.TextTooLong(20002, 20001)
      )
    }

    it("rejects an edge whose substring is out of bounds") {
      LongestRepeatProblem.from("AC$", 2, Vector(SuffixTreeEdge("a", "b", 5, 1))) shouldBe Left(
        LongestRepeatProblemError.EdgeOutOfBounds(0, 5, 1, 3)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.LongestRepeatProblem("AC$", 2, Vector.empty[bio.domain.graph.SuffixTreeEdge])"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.LongestRepeatProblem.from("AC$", 2, Vector.empty[bio.domain.graph.SuffixTreeEdge]).toOption.get.copy()"""
      )
    }
  }
}
