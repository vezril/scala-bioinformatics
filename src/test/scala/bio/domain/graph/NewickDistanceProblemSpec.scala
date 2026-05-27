package bio.domain.graph

import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class NewickDistanceProblemSpec extends AnyFunSpec with Matchers {

  private val catDogTree: NewickTree =
    NewickParser
      .parse("(cat)dog;")
      .getOrElse(sys.error("could not parse `(cat)dog;` fixture"))

  describe("NewickDistanceProblem.from") {
    it("accepts a query whose endpoints both exist in the tree") {
      val result = NewickDistanceProblem.from(catDogTree, "dog", "cat")
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.tree shouldBe catDogTree
      problem.x shouldBe "dog"
      problem.y shouldBe "cat"
    }

    it("rejects an unknown source label as UnknownLabel(x)") {
      NewickDistanceProblem.from(catDogTree, "fish", "cat") shouldBe
        Left(NewickDistanceProblemError.UnknownLabel("fish"))
    }

    it("rejects an unknown target label as UnknownLabel(y)") {
      NewickDistanceProblem.from(catDogTree, "dog", "fish") shouldBe
        Left(NewickDistanceProblemError.UnknownLabel("fish"))
    }

    it("reports the source label first when both are unknown") {
      NewickDistanceProblem.from(catDogTree, "fish", "bird") shouldBe
        Left(NewickDistanceProblemError.UnknownLabel("fish"))
    }
  }

  describe("NewickDistanceProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.NewickDistanceProblem(catDogTree, "dog", "cat")"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.NewickDistanceProblem.from(catDogTree, "dog", "cat").toOption.get.copy(x = "fish")"""
      )
    }
  }
}
