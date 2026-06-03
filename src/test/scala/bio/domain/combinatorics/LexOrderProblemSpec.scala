package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LexOrderProblemSpec extends AnyFunSpec with Matchers {
  describe("LexOrderProblem.from") {
    it("accepts a valid alphabet and length") {
      val result = LexOrderProblem.from(Vector('D', 'N', 'A'), 3)
      result.map(p => (p.alphabet, p.maxLength)) shouldBe Right((Vector('D', 'N', 'A'), 3))
    }

    it("rejects an empty alphabet") {
      LexOrderProblem.from(Vector.empty, 3) shouldBe Left(LexOrderProblemError.EmptyAlphabet)
    }

    it("rejects more than twelve symbols") {
      val alphabet = ('A' to 'M').toVector // 13 symbols
      LexOrderProblem.from(alphabet, 2) shouldBe Left(
        LexOrderProblemError.TooManySymbols(13, 12)
      )
    }

    it("rejects a duplicate symbol") {
      LexOrderProblem.from(Vector('A', 'B', 'A'), 2) shouldBe Left(
        LexOrderProblemError.DuplicateSymbol('A')
      )
    }

    it("rejects a non-positive length") {
      LexOrderProblem.from(Vector('A'), 0) shouldBe Left(
        LexOrderProblemError.NonPositiveLength(0)
      )
    }

    it("rejects a length over the cap") {
      LexOrderProblem.from(Vector('A'), 5) shouldBe Left(
        LexOrderProblemError.LengthExceedsMaximum(5, 4)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.combinatorics.LexOrderProblem(Vector('A'), 1)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """LexOrderProblem.from(Vector('A'), 1).toOption.get.copy(maxLength = 2)"""
      )
    }
  }
}
