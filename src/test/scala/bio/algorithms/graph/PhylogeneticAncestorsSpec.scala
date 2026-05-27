package bio.algorithms.graph

import bio.domain.graph.UnrootedBinaryTreeLeafCount
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PhylogeneticAncestorsSpec extends AnyFunSpec with Matchers {

  private def problem(n: Int): UnrootedBinaryTreeLeafCount =
    UnrootedBinaryTreeLeafCount
      .from(n)
      .getOrElse(sys.error(s"invalid UnrootedBinaryTreeLeafCount fixture (n=$n)"))

  describe("PhylogeneticAncestors.internalNodes") {
    it("returns 2 for n = 4 (the canonical Rosalind INOD sample)") {
      PhylogeneticAncestors.internalNodes(problem(4)) shouldBe 2
    }

    it("returns 1 for n = 3 (the lower boundary)") {
      PhylogeneticAncestors.internalNodes(problem(3)) shouldBe 1
    }

    it("returns 9998 for n = 10000 (the upper boundary)") {
      PhylogeneticAncestors.internalNodes(problem(10000)) shouldBe 9998
    }

    it("returns 98 for n = 100 (mid-range sanity check)") {
      PhylogeneticAncestors.internalNodes(problem(100)) shouldBe 98
    }
  }
}
