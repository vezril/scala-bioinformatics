package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WeightedNewickTreeSpec extends AnyFunSpec with Matchers {

  describe("WeightedNewickTree") {
    it("exposes a leaf's label and empty children") {
      val leaf = WeightedNewickTree(Some("dog"), Vector.empty)
      leaf.label shouldBe Some("dog")
      leaf.children shouldBe empty
    }

    it("exposes an internal node's weighted children") {
      val leaf = WeightedNewickTree(Some("dog"), Vector.empty)
      val node = WeightedNewickTree(None, Vector(WeightedChild(leaf, 42.0)))
      node.children should have size 1
      node.children.head.weight shouldBe 42.0
      node.children.head.subtree shouldBe leaf
    }

    it("collects all labels in the subtree") {
      val tree = WeightedNewickTree(
        None,
        Vector(
          WeightedChild(WeightedNewickTree(Some("dog"), Vector.empty), 42.0),
          WeightedChild(WeightedNewickTree(Some("cat"), Vector.empty), 33.0)
        )
      )
      tree.labels shouldBe Set("dog", "cat")
    }
  }
}
