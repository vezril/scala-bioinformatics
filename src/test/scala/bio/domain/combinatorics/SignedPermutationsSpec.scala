package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SignedPermutationsSpec extends AnyFunSpec with Matchers {
  describe("SignedPermutations") {
    it("reports the count as the number of permutations") {
      val perms = Vector.fill(8)(Vector(1, 2))
      SignedPermutations(perms).count shouldBe 8
    }

    it("formats the count followed by the permutations") {
      SignedPermutations(
        Vector(Vector(1, 2), Vector(-1, 2))
      ).format shouldBe "2\n1 2\n-1 2"
    }
  }
}
