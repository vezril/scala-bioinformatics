package bio.algorithms.combinatorics

import bio.domain.combinatorics.{Permutation, ReversalDistanceProblem}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversalDistanceSearchSpec extends AnyFunSpec with Matchers {

  private def perm(values: Int*): Permutation =
    Permutation.from(values.toVector).getOrElse(fail(s"invalid permutation: $values"))

  private def dist(source: Permutation, target: Permutation): Int =
    ReversalDistanceSearch
      .distance(ReversalDistanceProblem.from(source, target).getOrElse(fail("invalid problem")))
      .distance

  describe("ReversalDistanceSearch.distance") {
    it("returns zero for identical permutations") {
      dist(
        perm(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
        perm(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
      ) shouldBe 0
    }

    it("returns one when the permutations are a single reversal apart") {
      dist(perm(1, 2, 3, 4, 5), perm(1, 4, 3, 2, 5)) shouldBe 1
    }

    it("returns zero for two empty permutations") {
      dist(perm(), perm()) shouldBe 0
    }

    it("matches the canonical Rosalind sample") {
      val pairs = Vector(
        (perm(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), perm(3, 1, 5, 2, 7, 4, 9, 6, 10, 8)),
        (perm(3, 10, 8, 2, 5, 4, 7, 1, 6, 9), perm(5, 2, 3, 1, 7, 4, 10, 8, 6, 9)),
        (perm(8, 6, 7, 9, 4, 1, 3, 10, 2, 5), perm(8, 2, 7, 6, 9, 1, 5, 3, 10, 4)),
        (perm(3, 9, 10, 4, 1, 8, 6, 7, 5, 2), perm(2, 9, 8, 5, 1, 7, 3, 4, 6, 10)),
        (perm(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), perm(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
      )
      pairs.map { case (s, t) => dist(s, t) } shouldBe Vector(9, 4, 5, 7, 0)
    }
  }
}
