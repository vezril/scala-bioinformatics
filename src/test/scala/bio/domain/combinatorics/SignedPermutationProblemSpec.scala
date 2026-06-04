package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SignedPermutationProblemSpec extends AnyFunSpec with Matchers {
  describe("SignedPermutationProblem.from") {
    it("accepts a valid length") {
      SignedPermutationProblem.from(2).map(_.n) shouldBe Right(2)
    }

    it("rejects a non-positive length") {
      SignedPermutationProblem.from(0) shouldBe Left(SignedPermutationProblemError.NonPositive(0))
    }

    it("rejects a length over the cap") {
      SignedPermutationProblem.from(7) shouldBe Left(
        SignedPermutationProblemError.ExceedsMaximum(7, 6)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.combinatorics.SignedPermutationProblem(2)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile("""SignedPermutationProblem.from(2).toOption.get.copy(n = 3)""")
    }
  }
}
