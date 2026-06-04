package bio.algorithms.combinatorics

import bio.domain.combinatorics.SignedPermutationProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SignedPermutationEnumerationSpec extends AnyFunSpec with Matchers {

  private def enumerate(n: Int): Vector[Vector[Int]] = {
    val problem = SignedPermutationProblem.from(n).getOrElse(fail("invalid problem"))
    SignedPermutationEnumeration.enumerate(problem).permutations
  }

  describe("SignedPermutationEnumeration.enumerate") {
    it("matches the canonical Rosalind sample") {
      val result = enumerate(2)
      result.size shouldBe 8
      result.toSet shouldBe Set(
        Vector(-1, -2), Vector(-1, 2), Vector(1, -2), Vector(1, 2),
        Vector(-2, -1), Vector(-2, 1), Vector(2, -1), Vector(2, 1)
      )
    }

    it("yields two signed permutations for length one") {
      val result = enumerate(1)
      result.size shouldBe 2
      result.toSet shouldBe Set(Vector(-1), Vector(1))
    }

    it("follows the n! * 2^n count formula") {
      enumerate(3).size shouldBe 48
    }

    it("yields only valid signed orderings") {
      enumerate(3).foreach { perm =>
        perm.length shouldBe 3
        perm.map(_.abs).sorted shouldBe Vector(1, 2, 3)
      }
    }
  }
}
