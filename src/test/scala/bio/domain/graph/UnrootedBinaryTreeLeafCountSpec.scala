package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UnrootedBinaryTreeLeafCountSpec extends AnyFunSpec with Matchers {

  describe("UnrootedBinaryTreeLeafCount.from") {
    it("accepts n = 4 (the canonical Rosalind INOD sample)") {
      UnrootedBinaryTreeLeafCount.from(4).map(_.n) shouldBe Right(4)
    }

    it("accepts n = 3 (the lower boundary)") {
      UnrootedBinaryTreeLeafCount.from(3).map(_.n) shouldBe Right(3)
    }

    it("accepts n = 10000 (the upper boundary)") {
      UnrootedBinaryTreeLeafCount.from(10000).map(_.n) shouldBe Right(10000)
    }

    it("rejects n = 2 as BelowMinimum(2, 3)") {
      UnrootedBinaryTreeLeafCount.from(2) shouldBe
        Left(UnrootedBinaryTreeLeafCountError.BelowMinimum(2, 3))
    }

    it("rejects n = 0 as BelowMinimum(0, 3)") {
      UnrootedBinaryTreeLeafCount.from(0) shouldBe
        Left(UnrootedBinaryTreeLeafCountError.BelowMinimum(0, 3))
    }

    it("rejects n = -5 as BelowMinimum(-5, 3)") {
      UnrootedBinaryTreeLeafCount.from(-5) shouldBe
        Left(UnrootedBinaryTreeLeafCountError.BelowMinimum(-5, 3))
    }

    it("rejects n = 10001 as ExceedsMaximum(10001, 10000)") {
      UnrootedBinaryTreeLeafCount.from(10001) shouldBe
        Left(UnrootedBinaryTreeLeafCountError.ExceedsMaximum(10001, 10000))
    }
  }

  describe("UnrootedBinaryTreeLeafCount construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.graph.UnrootedBinaryTreeLeafCount(4)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.UnrootedBinaryTreeLeafCount.from(4).toOption.get.copy(n = 7)"""
      )
    }
  }
}
