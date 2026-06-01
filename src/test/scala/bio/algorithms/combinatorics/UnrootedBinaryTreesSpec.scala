package bio.algorithms.combinatorics

import bio.domain.combinatorics.LeafCount
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UnrootedBinaryTreesSpec extends AnyFunSpec with Matchers {

  private def leafCount(n: Int): LeafCount =
    LeafCount.from(n).getOrElse(sys.error(s"invalid LeafCount fixture: $n"))

  describe("UnrootedBinaryTrees.count") {
    it("returns 15 for the Rosalind sample (n=5)") {
      UnrootedBinaryTrees.count(leafCount(5)) shouldBe 15
    }

    it("returns 3 for n=4") {
      UnrootedBinaryTrees.count(leafCount(4)) shouldBe 3
    }

    it("returns 1 for n=3 (a single tree)") {
      UnrootedBinaryTrees.count(leafCount(3)) shouldBe 1
    }

    it("returns 1 for n=2 (empty product edge case)") {
      UnrootedBinaryTrees.count(leafCount(2)) shouldBe 1
    }

    it("returns 1 for n=1 (empty product edge case)") {
      UnrootedBinaryTrees.count(leafCount(1)) shouldBe 1
    }

    it("returns 105 for n=6") {
      UnrootedBinaryTrees.count(leafCount(6)) shouldBe 105
    }

    it("stays within [0, 999999] at the upper bound (n=1000)") {
      val result = UnrootedBinaryTrees.count(leafCount(1000))
      result should be >= 0
      result should be <= 999999
    }
  }
}
