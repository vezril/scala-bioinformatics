package bio.domain.graph

import bio.parsing.WeightedNewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WeightedTreeDistanceProblemSpec extends AnyFunSpec with Matchers {

  private val tree =
    WeightedNewickParser.parse("(dog:42,cat:33);").getOrElse(sys.error("bad fixture"))

  describe("WeightedTreeDistanceProblemError") {
    it("constructs NodeNotFound carrying the label") {
      val err: WeightedTreeDistanceProblemError = WeightedTreeDistanceProblemError.NodeNotFound("fish")
      err shouldBe WeightedTreeDistanceProblemError.NodeNotFound("fish")
    }
  }

  describe("WeightedTreeDistanceProblem.from") {
    it("accepts two labels present in the tree") {
      WeightedTreeDistanceProblem.from(tree, "cat", "dog").isRight shouldBe true
    }

    it("rejects a missing first label") {
      WeightedTreeDistanceProblem.from(tree, "fish", "dog") shouldBe Left(
        WeightedTreeDistanceProblemError.NodeNotFound("fish")
      )
    }

    it("rejects a missing second label") {
      WeightedTreeDistanceProblem.from(tree, "dog", "fish") shouldBe Left(
        WeightedTreeDistanceProblemError.NodeNotFound("fish")
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.WeightedTreeDistanceProblem(bio.domain.graph.WeightedNewickTree(None, Vector.empty), "a", "b")"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.WeightedTreeDistanceProblem.from(bio.parsing.WeightedNewickParser.parse("(dog:1,cat:1);").toOption.get, "dog", "cat").toOption.get.copy()"""
      )
    }
  }
}
