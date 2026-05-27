package bio.algorithms.graph

import bio.domain.graph.{TreeCompletionProblem, UndirectedEdge}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TreeCompletionSpec extends AnyFunSpec with Matchers {

  private def edge(u: Int, v: Int): UndirectedEdge =
    UndirectedEdge.from(u, v).getOrElse(sys.error(s"invalid edge fixture ($u, $v)"))

  private def problem(n: Int, edges: Vector[UndirectedEdge]): TreeCompletionProblem =
    TreeCompletionProblem
      .from(n, edges)
      .getOrElse(sys.error(s"invalid TreeCompletionProblem fixture (n=$n)"))

  describe("TreeCompletion.edgesToAdd") {
    it("returns 3 for the Rosalind sample (n=10, 6 edges)") {
      val edges = Vector(edge(1, 2), edge(2, 8), edge(4, 10), edge(5, 9), edge(6, 10), edge(7, 9))
      TreeCompletion.edgesToAdd(problem(10, edges)) shouldBe 3
    }

    it("returns 0 for a single isolated node (n=1, no edges)") {
      TreeCompletion.edgesToAdd(problem(1, Vector.empty)) shouldBe 0
    }

    it("returns 0 for a graph that is already a tree (n=5, 4 edges)") {
      val edges = Vector(edge(1, 2), edge(2, 3), edge(3, 4), edge(4, 5))
      TreeCompletion.edgesToAdd(problem(5, edges)) shouldBe 0
    }

    it("returns n-1 for a fully disconnected graph (n=10, no edges)") {
      TreeCompletion.edgesToAdd(problem(10, Vector.empty)) shouldBe 9
    }

    it("returns 1 for two disjoint trees (n=6, 4 edges across two 3-node paths)") {
      val edges = Vector(edge(1, 2), edge(2, 3), edge(4, 5), edge(5, 6))
      TreeCompletion.edgesToAdd(problem(6, edges)) shouldBe 1
    }
  }
}
