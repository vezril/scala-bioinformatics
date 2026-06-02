package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UnrootedBinaryTreesResultSpec extends AnyFunSpec with Matchers {

  describe("UnrootedBinaryTrees result") {
    it("exposes the Newick strings") {
      val trees = Vector("((cat,mouse))dog;", "((mouse,cat))dog;")
      UnrootedBinaryTrees(trees).trees shouldBe trees
    }

    it("formats one tree per line") {
      UnrootedBinaryTrees(Vector("((cat,mouse))dog;", "((mouse,cat))dog;")).format shouldBe
        "((cat,mouse))dog;\n((mouse,cat))dog;"
    }

    it("renders the empty result as the empty string") {
      UnrootedBinaryTrees(Vector.empty).format shouldBe ""
    }
  }
}
