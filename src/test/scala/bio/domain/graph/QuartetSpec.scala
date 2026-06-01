package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class QuartetSpec extends AnyFunSpec with Matchers {

  describe("Quartet.of") {
    it("is invariant to the order of the two taxa within a side") {
      Quartet.of("dog", "elephant", "rabbit", "robot") shouldBe
        Quartet.of("elephant", "dog", "robot", "rabbit")
    }

    it("is invariant to the order of the two sides") {
      Quartet.of("dog", "elephant", "rabbit", "robot") shouldBe
        Quartet.of("rabbit", "robot", "dog", "elephant")
    }
  }

  describe("Quartet.render") {
    it("produces the two-brace-pair format with canonical ordering") {
      Quartet.of("dog", "elephant", "rabbit", "robot").render shouldBe
        "{dog, elephant} {rabbit, robot}"
    }

    it("canonicalises within and across sides before rendering") {
      Quartet.of("robot", "rabbit", "elephant", "dog").render shouldBe
        "{dog, elephant} {rabbit, robot}"
    }
  }

  describe("Quartet construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.graph.Quartet(("a", "b"), ("c", "d"))""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.Quartet.of("a", "b", "c", "d").copy(pairA = ("e", "f"))"""
      )
    }
  }
}
