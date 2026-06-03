package bio.algorithms.graph

import bio.domain.graph.WeightedTreeDistanceProblem
import bio.parsing.WeightedNewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WeightedNewickDistanceSpec extends AnyFunSpec with Matchers {

  private def distance(newick: String, x: String, y: String): Double =
    WeightedNewickDistance.between(
      WeightedTreeDistanceProblem
        .from(
          WeightedNewickParser.parse(newick).getOrElse(sys.error(s"bad Newick: $newick")),
          x,
          y
        )
        .getOrElse(sys.error("invalid WeightedTreeDistanceProblem fixture"))
    )

  describe("WeightedNewickDistance.between") {
    it("computes the first canonical NKEW distance") {
      distance("(dog:42,cat:33);", "cat", "dog") shouldBe 75.0
    }

    it("computes a distance across an internal node") {
      distance("((dog:4,cat:3):74,robot:98,elephant:58);", "dog", "elephant") shouldBe 136.0
    }

    it("returns zero for the distance from a node to itself") {
      distance("(dog:42,cat:33);", "dog", "dog") shouldBe 0.0
    }
  }
}
