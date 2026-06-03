package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PermutationSpec extends AnyFunSpec with Matchers {
  describe("Permutation.from") {
    it("accepts a valid permutation") {
      val values = Vector(5, 1, 4, 2, 3)
      Permutation.from(values).map(_.values) shouldBe Right(values)
    }

    it("accepts the empty permutation") {
      Permutation.from(Vector.empty).map(_.values) shouldBe Right(Vector.empty[Int])
    }

    it("rejects a sequence longer than the bound") {
      val values = Vector.range(1, 10002) // length 10001
      Permutation.from(values) shouldBe Left(PermutationError.TooLong(10001, 10000))
    }

    it("rejects a sequence that is not a permutation of 1..n") {
      Permutation.from(Vector(1, 2, 2)) shouldBe Left(
        PermutationError.NotAPermutation(Vector(1, 2, 2))
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.combinatorics.Permutation(Vector(1, 2, 3))""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """Permutation.from(Vector(1, 2, 3)).toOption.get.copy(values = Vector(3, 2, 1))"""
      )
    }
  }
}
