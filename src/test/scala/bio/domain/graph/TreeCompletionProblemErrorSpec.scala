package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TreeCompletionProblemErrorSpec extends AnyFunSpec with Matchers {

  private def edge(u: Int, v: Int): UndirectedEdge =
    UndirectedEdge.from(u, v).getOrElse(sys.error(s"invalid edge fixture ($u, $v)"))

  describe("TreeCompletionProblemError.NonPositiveN") {
    it("carries the offending value") {
      TreeCompletionProblemError.NonPositiveN(0).value shouldBe 0
    }
  }

  describe("TreeCompletionProblemError.NExceedsMaximum") {
    it("carries the offending value and the maximum") {
      val err = TreeCompletionProblemError.NExceedsMaximum(1001, 1000)
      err.value shouldBe 1001
      err.max shouldBe 1000
    }
  }

  describe("TreeCompletionProblemError.EdgeEndpointOutOfRange") {
    it("carries the offending edge and n") {
      val e   = edge(3, 11)
      val err = TreeCompletionProblemError.EdgeEndpointOutOfRange(e, 10)
      err.edge shouldBe e
      err.n shouldBe 10
    }
  }
}
