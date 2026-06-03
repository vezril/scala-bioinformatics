package bio.algorithms.combinatorics

import bio.domain.combinatorics.Permutation
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LongestSubsequencesSpec extends AnyFunSpec with Matchers {

  private def perm(values: Int*): Permutation =
    Permutation.from(values.toVector).getOrElse(fail(s"invalid permutation: $values"))

  /** True iff `sub` is a subsequence of `seq` (order-preserving, not necessarily contiguous). */
  private def isSubsequence(sub: Vector[Int], seq: Vector[Int]): Boolean = {
    var i = 0
    seq.foreach(x => if (i < sub.length && sub(i) == x) i += 1)
    i == sub.length
  }

  private def strictlyIncreasing(v: Vector[Int]): Boolean =
    v.lazyZip(v.drop(1)).forall { case (a, b) => a < b }

  private def strictlyDecreasing(v: Vector[Int]): Boolean =
    v.lazyZip(v.drop(1)).forall { case (a, b) => a > b }

  describe("LongestSubsequences.find") {
    it("produces valid longest subsequences for the canonical sample") {
      val input  = Vector(5, 1, 4, 2, 3)
      val result = LongestSubsequences.find(perm(5, 1, 4, 2, 3))

      result.increasing.length shouldBe 3
      strictlyIncreasing(result.increasing) shouldBe true
      isSubsequence(result.increasing, input) shouldBe true

      result.decreasing.length shouldBe 3
      strictlyDecreasing(result.decreasing) shouldBe true
      isSubsequence(result.decreasing, input) shouldBe true
    }

    it("returns a full-length increasing run for a sorted-ascending permutation") {
      val result = LongestSubsequences.find(perm(1, 2, 3, 4))
      result.increasing shouldBe Vector(1, 2, 3, 4)
      result.decreasing.length shouldBe 1
    }

    it("yields that element for both on a single-element permutation") {
      val result = LongestSubsequences.find(perm(1))
      result.increasing shouldBe Vector(1)
      result.decreasing shouldBe Vector(1)
    }

    it("yields two empty subsequences for the empty permutation") {
      val result = LongestSubsequences.find(perm())
      result.increasing shouldBe Vector.empty[Int]
      result.decreasing shouldBe Vector.empty[Int]
    }
  }
}
