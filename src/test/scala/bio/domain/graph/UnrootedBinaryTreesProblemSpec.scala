package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UnrootedBinaryTreesProblemSpec extends AnyFunSpec with Matchers {

  describe("UnrootedBinaryTreesProblem.from") {
    it("accepts a valid collection of taxa, preserving them") {
      val taxa = Vector("dog", "cat", "mouse", "elephant")
      UnrootedBinaryTreesProblem.from(taxa).map(_.taxa) shouldBe Right(taxa)
    }

    it("accepts the minimum of three taxa") {
      UnrootedBinaryTreesProblem.from(Vector("dog", "cat", "mouse")).isRight shouldBe true
    }

    it("rejects fewer than three taxa") {
      UnrootedBinaryTreesProblem.from(Vector("dog", "cat")) shouldBe Left(
        UnrootedBinaryTreesProblemError.TooFewTaxa(2, 3)
      )
    }

    it("rejects more than ten taxa") {
      val taxa = (1 to 11).map(i => s"t$i").toVector
      UnrootedBinaryTreesProblem.from(taxa) shouldBe Left(
        UnrootedBinaryTreesProblemError.TooManyTaxa(11, 10)
      )
    }

    it("rejects duplicate taxa") {
      UnrootedBinaryTreesProblem.from(Vector("dog", "cat", "cat")) shouldBe Left(
        UnrootedBinaryTreesProblemError.DuplicateTaxon("cat")
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.UnrootedBinaryTreesProblem(Vector("dog", "cat", "mouse"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.UnrootedBinaryTreesProblem.from(Vector("dog", "cat", "mouse")).toOption.get.copy()"""
      )
    }
  }
}
