package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class KmerEnumerationProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("KmerEnumerationProblemError") {
    it("provides EmptyAlphabet as a case object") {
      val err: KmerEnumerationProblemError =
        KmerEnumerationProblemError.EmptyAlphabet
      err shouldBe KmerEnumerationProblemError.EmptyAlphabet
    }

    it("carries the count and the maximum in TooManySymbols") {
      val err = KmerEnumerationProblemError.TooManySymbols(11, 10)
      err.count shouldBe 11
      err.max shouldBe 10
    }

    it("carries the repeated character in DuplicateSymbol") {
      val err = KmerEnumerationProblemError.DuplicateSymbol('A')
      err.symbol shouldBe 'A'
    }

    it("carries the requested length in NonPositiveLength") {
      val err = KmerEnumerationProblemError.NonPositiveLength(0)
      err.length shouldBe 0
    }

    it("carries the requested length and the maximum in LengthExceedsMaximum") {
      val err = KmerEnumerationProblemError.LengthExceedsMaximum(11, 10)
      err.length shouldBe 11
      err.max shouldBe 10
    }
  }
}
