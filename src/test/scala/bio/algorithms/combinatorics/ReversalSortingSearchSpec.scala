package bio.algorithms.combinatorics

import bio.domain.combinatorics.{Permutation, Reversal, ReversalDistanceProblem}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversalSortingSearchSpec extends AnyFunSpec with Matchers {

  private def perm(values: Int*): Permutation =
    Permutation.from(values.toVector).getOrElse(fail(s"invalid permutation: $values"))

  private def problem(source: Permutation, target: Permutation): ReversalDistanceProblem =
    ReversalDistanceProblem.from(source, target).getOrElse(fail("invalid problem"))

  /** Apply a single 1-based interval reversal to `v`. */
  private def applyReversal(v: Vector[Int], r: Reversal): Vector[Int] = {
    val (i, j)            = (r.from - 1, r.to - 1)
    val (pre, rest)       = v.splitAt(i)
    val (mid, post)       = rest.splitAt(j - i + 1)
    pre ++ mid.reverse ++ post
  }

  private def applyAll(v: Vector[Int], rs: Vector[Reversal]): Vector[Int] =
    rs.foldLeft(v)(applyReversal)

  describe("ReversalSortingSearch.sort") {
    it("matches the canonical sample distance and sorts the permutation") {
      val source = perm(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
      val target = perm(1, 8, 9, 3, 2, 7, 6, 5, 4, 10)
      val result = ReversalSortingSearch.sort(problem(source, target))

      result.distance shouldBe 2
      result.reversals.length shouldBe 2
      applyAll(source.values, result.reversals) shouldBe target.values
    }

    it("needs no reversals for identical permutations") {
      val p      = perm(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
      val result = ReversalSortingSearch.sort(problem(p, p))
      result.distance shouldBe 0
      result.reversals shouldBe Vector.empty[Reversal]
    }

    it("yields one sorting reversal when a single reversal apart") {
      val source = perm(1, 2, 3, 4, 5)
      val target = perm(1, 4, 3, 2, 5)
      val result = ReversalSortingSearch.sort(problem(source, target))
      result.distance shouldBe 1
      result.reversals.length shouldBe 1
      applyAll(source.values, result.reversals) shouldBe target.values
    }

    it("returns only valid intervals (1 <= from < to <= n)") {
      val source = perm(3, 1, 5, 2, 7, 4, 9, 6, 10, 8)
      val target = perm(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
      val result = ReversalSortingSearch.sort(problem(source, target))
      val n      = source.values.length
      result.reversals.foreach { r =>
        r.from should be >= 1
        r.to should be <= n
        r.from should be < r.to
      }
      applyAll(source.values, result.reversals) shouldBe target.values
    }
  }
}
