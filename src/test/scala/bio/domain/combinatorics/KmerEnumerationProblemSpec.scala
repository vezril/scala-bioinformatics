package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class KmerEnumerationProblemSpec extends AnyFunSpec with Matchers {

  private val sampleAlphabet = Vector('A', 'C', 'G', 'T')

  describe("KmerEnumerationProblem.from") {
    it("accepts the canonical sample and preserves alphabet order") {
      val result = KmerEnumerationProblem.from(sampleAlphabet, 2)
      result.isRight shouldBe true
      result.foreach { p =>
        p.alphabet shouldBe sampleAlphabet
        p.length shouldBe 2
      }
    }

    it("rejects an empty alphabet") {
      KmerEnumerationProblem.from(Vector.empty, 2) shouldBe
        Left(KmerEnumerationProblemError.EmptyAlphabet)
    }

    it("rejects an alphabet with more than 10 symbols") {
      val eleven = ('a' to 'k').toVector
      KmerEnumerationProblem.from(eleven, 2) shouldBe
        Left(KmerEnumerationProblemError.TooManySymbols(11, 10))
    }

    it("rejects an alphabet containing a duplicate symbol") {
      KmerEnumerationProblem.from(Vector('A', 'C', 'A'), 2) shouldBe
        Left(KmerEnumerationProblemError.DuplicateSymbol('A'))
    }

    it("rejects a non-positive length") {
      KmerEnumerationProblem.from(sampleAlphabet, 0) shouldBe
        Left(KmerEnumerationProblemError.NonPositiveLength(0))
    }

    it("rejects a length greater than 10") {
      KmerEnumerationProblem.from(sampleAlphabet, 11) shouldBe
        Left(KmerEnumerationProblemError.LengthExceedsMaximum(11, 10))
    }

    it("reports a duplicate symbol before a non-positive length") {
      KmerEnumerationProblem.from(Vector('A', 'A'), 0) shouldBe
        Left(KmerEnumerationProblemError.DuplicateSymbol('A'))
    }

    it("reports empty alphabet before any other failure") {
      KmerEnumerationProblem.from(Vector.empty, 0) shouldBe
        Left(KmerEnumerationProblemError.EmptyAlphabet)
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.KmerEnumerationProblem(Vector('A', 'C'), 2)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """KmerEnumerationProblem
          |  .from(Vector('A', 'C'), 2)
          |  .toOption
          |  .get
          |  .copy(length = 3)""".stripMargin
      )
    }
  }
}
