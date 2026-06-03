package bio.parsing

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WeightedNewickParserSpec extends AnyFunSpec with Matchers {

  describe("WeightedNewickParser.parse") {
    it("parses a simple weighted tree") {
      val tree = WeightedNewickParser.parse("(dog:42,cat:33);").getOrElse(fail("should parse"))
      tree.children.map(c => (c.subtree.label, c.weight)) shouldBe Vector(
        (Some("dog"), 42.0),
        (Some("cat"), 33.0)
      )
    }

    it("parses a nested weighted tree") {
      val tree =
        WeightedNewickParser
          .parse("((dog:4,cat:3):74,robot:98,elephant:58);")
          .getOrElse(fail("should parse"))
      tree.children should have size 3
      val internal = tree.children.head
      internal.weight shouldBe 74.0
      internal.subtree.labels shouldBe Set("dog", "cat")
    }

    it("rejects input without a terminator") {
      WeightedNewickParser.parse("(dog:42,cat:33)").isLeft shouldBe true
    }

    it("rejects a non-numeric edge weight") {
      WeightedNewickParser.parse("(dog:xx,cat:33);").isLeft shouldBe true
    }
  }
}
