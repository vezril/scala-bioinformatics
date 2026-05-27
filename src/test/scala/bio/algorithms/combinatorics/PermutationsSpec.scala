package bio.algorithms.combinatorics

import bio.domain.combinatorics.PermutationLength
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PermutationsSpec extends AnyFunSpec with Matchers {

  private def pl(n: Int): PermutationLength =
    PermutationLength
      .from(n)
      .getOrElse(sys.error(s"invalid PermutationLength in fixture: $n"))

  describe("Permutations.enumerate") {
    it("produces Vector(Vector(1)) for n=1") {
      Permutations.enumerate(pl(1)) shouldBe Vector(Vector(1))
    }

    it("produces both orderings for n=2") {
      val result = Permutations.enumerate(pl(2))
      result        should have size 2
      result        should contain(Vector(1, 2))
      result        should contain(Vector(2, 1))
    }

    it("produces all six permutations for the Rosalind sample n=3") {
      val result = Permutations.enumerate(pl(3))
      result should have size 6
      result should contain allOf (
        Vector(1, 2, 3),
        Vector(1, 3, 2),
        Vector(2, 1, 3),
        Vector(2, 3, 1),
        Vector(3, 1, 2),
        Vector(3, 2, 1)
      )
    }

    it("produces 5040 permutations for n=7 (= 7!)") {
      Permutations.enumerate(pl(7)) should have size 5040
    }

    it("produces permutations of length n for any valid input") {
      Permutations.enumerate(pl(5)).foreach { perm =>
        perm.size shouldBe 5
      }
    }

    it("ensures every permutation contains each of 1..n exactly once") {
      val n        = 4
      val expected = (1 to n).toSet
      Permutations.enumerate(pl(n)).foreach { perm =>
        perm.toSet shouldBe expected
      }
    }

    it("returns no duplicate permutations") {
      val result = Permutations.enumerate(pl(5))
      result.distinct.size shouldBe result.size
    }
  }
}
