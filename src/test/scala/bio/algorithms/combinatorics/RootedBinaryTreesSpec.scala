package bio.algorithms.combinatorics

import bio.domain.combinatorics.RootedTreeLeafCount
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RootedBinaryTreesSpec extends AnyFunSpec with Matchers {

  private def count(n: Int): Int =
    RootedBinaryTrees.count(
      RootedTreeLeafCount.from(n).getOrElse(sys.error(s"invalid RootedTreeLeafCount: $n"))
    )

  describe("RootedBinaryTrees.count") {
    it("computes the canonical Rosalind ROOT sample (n = 4)") {
      count(4) shouldBe 15
    }

    it("counts one tree for a single taxon") {
      count(1) shouldBe 1
    }

    it("counts one tree for two taxa") {
      count(2) shouldBe 1
    }

    it("counts three trees for three taxa") {
      count(3) shouldBe 3
    }

    it("reduces large counts modulo 1,000,000") {
      // (2*10 - 3)!! = 17!! = 34,459,425 ; mod 1,000,000 = 459,425
      count(10) shouldBe 459425
    }
  }
}
