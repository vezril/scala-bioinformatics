package bio.algorithms.graph

import bio.domain.graph.UnrootedBinaryTreesProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EnumerateUnrootedBinaryTreesSpec extends AnyFunSpec with Matchers {

  private def trees(taxa: String*): Vector[String] =
    EnumerateUnrootedBinaryTrees
      .enumerate(
        UnrootedBinaryTreesProblem
          .from(taxa.toVector)
          .getOrElse(sys.error("invalid UnrootedBinaryTreesProblem fixture"))
      )
      .trees

  describe("EnumerateUnrootedBinaryTrees.enumerate") {
    it("enumerates the canonical Rosalind EUBT sample") {
      trees("dog", "cat", "mouse", "elephant") should contain theSameElementsAs Vector(
        "(((cat,mouse),elephant))dog;",
        "(((cat,elephant),mouse))dog;",
        "((cat,(mouse,elephant)))dog;"
      )
    }

    it("produces the single tree for three taxa") {
      trees("dog", "cat", "mouse") shouldBe Vector("((cat,mouse))dog;")
    }

    it("produces (2n-5)!! = 15 distinct trees for five taxa") {
      val result = trees("a", "b", "c", "d", "e")
      result should have size 15
      result.distinct should have size 15
    }

    it("roots every tree at the first taxon") {
      trees("dog", "cat", "mouse", "elephant").foreach(_ should endWith(")dog;"))
    }
  }
}
