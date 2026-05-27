package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TreeCompletionProblemSpec extends AnyFunSpec with Matchers {

  private def edge(u: Int, v: Int): UndirectedEdge =
    UndirectedEdge.from(u, v).getOrElse(sys.error(s"invalid edge fixture ($u, $v)"))

  private val rosalindSampleEdges: Vector[UndirectedEdge] =
    Vector(edge(1, 2), edge(2, 8), edge(4, 10), edge(5, 9), edge(6, 10), edge(7, 9))

  describe("TreeCompletionProblem.from") {
    it("accepts the Rosalind sample (n=10, 6 edges)") {
      val problem = TreeCompletionProblem.from(10, rosalindSampleEdges).toOption.get
      problem.n shouldBe 10
      problem.edges shouldBe rosalindSampleEdges
    }

    it("accepts the minimum valid input (n=1, no edges)") {
      val problem = TreeCompletionProblem.from(1, Vector.empty).toOption.get
      problem.n shouldBe 1
      problem.edges shouldBe Vector.empty
    }

    it("accepts the upper-bound n (1000)") {
      TreeCompletionProblem.from(1000, Vector.empty) shouldBe a[Right[_, _]]
    }

    it("rejects n = 0 as NonPositiveN(0)") {
      TreeCompletionProblem.from(0, Vector.empty) shouldBe
        Left(TreeCompletionProblemError.NonPositiveN(0))
    }

    it("rejects n = 1001 as NExceedsMaximum(1001, 1000)") {
      TreeCompletionProblem.from(1001, Vector.empty) shouldBe
        Left(TreeCompletionProblemError.NExceedsMaximum(1001, 1000))
    }

    it("rejects an edge with u > n as EdgeEndpointOutOfRange") {
      val bad = edge(6, 1)
      TreeCompletionProblem.from(5, Vector(bad)) shouldBe
        Left(TreeCompletionProblemError.EdgeEndpointOutOfRange(bad, 5))
    }

    it("rejects an edge with v > n as EdgeEndpointOutOfRange") {
      val bad = edge(1, 6)
      TreeCompletionProblem.from(5, Vector(bad)) shouldBe
        Left(TreeCompletionProblemError.EdgeEndpointOutOfRange(bad, 5))
    }

    it("returns the first offending edge in input order when multiple are out of range") {
      val first   = edge(6, 3)
      val edges   = Vector(edge(1, 2), first, edge(1, 7))
      TreeCompletionProblem.from(5, edges) shouldBe
        Left(TreeCompletionProblemError.EdgeEndpointOutOfRange(first, 5))
    }

    it("validates n lower bound before n upper bound") {
      val edges = Vector(edge(1, 2))
      TreeCompletionProblem.from(0, edges) shouldBe
        Left(TreeCompletionProblemError.NonPositiveN(0))
    }

    it("validates n upper bound before edge endpoint scan") {
      val edges = Vector(edge(1, 2))
      TreeCompletionProblem.from(1001, edges) shouldBe
        Left(TreeCompletionProblemError.NExceedsMaximum(1001, 1000))
    }
  }

  describe("TreeCompletionProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.graph.TreeCompletionProblem(10, Vector.empty)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.TreeCompletionProblem
          |  .from(10, Vector.empty)
          |  .toOption.get.copy(n = 99)""".stripMargin
      )
    }
  }
}
