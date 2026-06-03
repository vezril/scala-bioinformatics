package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversalDistanceProblemSpec extends AnyFunSpec with Matchers {

  private def perm(values: Int*): Permutation =
    Permutation.from(values.toVector).getOrElse(fail(s"invalid permutation: $values"))

  describe("ReversalDistanceProblem.from") {
    it("accepts two equal-length permutations within the bound") {
      val source = perm(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
      val target = perm(3, 1, 5, 2, 7, 4, 9, 6, 10, 8)
      val result = ReversalDistanceProblem.from(source, target)
      result.map(_.source) shouldBe Right(source)
      result.map(_.target) shouldBe Right(target)
    }

    it("accepts two equal empty permutations") {
      val empty = perm()
      ReversalDistanceProblem.from(empty, empty).map(_.source) shouldBe Right(empty)
    }

    it("rejects permutations of differing length") {
      ReversalDistanceProblem.from(perm(1, 2, 3), perm(1, 2, 3, 4)) shouldBe Left(
        ReversalDistanceProblemError.LengthMismatch(3, 4)
      )
    }

    it("rejects permutations longer than the BFS-tractable bound") {
      val source = perm((1 to 11): _*)
      val target = perm((11 to 1 by -1): _*)
      ReversalDistanceProblem.from(source, target) shouldBe Left(
        ReversalDistanceProblemError.LengthExceedsMax(11, 10)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.ReversalDistanceProblem(
          |  bio.domain.combinatorics.Permutation.from(Vector(1, 2)).toOption.get,
          |  bio.domain.combinatorics.Permutation.from(Vector(2, 1)).toOption.get
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """ReversalDistanceProblem.from(
          |  Permutation.from(Vector(1, 2)).toOption.get,
          |  Permutation.from(Vector(2, 1)).toOption.get
          |).toOption.get.copy()""".stripMargin
      )
    }
  }
}
