package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MultipleAlignmentProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("MultipleAlignmentProblemError.WrongNumberOfStrings") {
    it("carries the actual count and the expected count") {
      val err = MultipleAlignmentProblemError.WrongNumberOfStrings(3, 4)
      err.actual shouldBe 3
      err.expected shouldBe 4
    }

    it("is value-equal when both fields match") {
      MultipleAlignmentProblemError.WrongNumberOfStrings(5, 4) shouldBe
        MultipleAlignmentProblemError.WrongNumberOfStrings(5, 4)
    }
  }

  describe("MultipleAlignmentProblemError.StringTooLong") {
    it("carries the offending index, length, and configured cap") {
      val err = MultipleAlignmentProblemError.StringTooLong(1, 11, 10)
      err.index shouldBe 1
      err.length shouldBe 11
      err.max shouldBe 10
    }

    it("is value-equal when all three fields match") {
      MultipleAlignmentProblemError.StringTooLong(2, 12, 10) shouldBe
        MultipleAlignmentProblemError.StringTooLong(2, 12, 10)
    }
  }

  describe("MultipleAlignmentProblemError as an ADT") {
    it("makes WrongNumberOfStrings and StringTooLong distinct subtypes") {
      val errs: List[MultipleAlignmentProblemError] = List(
        MultipleAlignmentProblemError.WrongNumberOfStrings(3, 4),
        MultipleAlignmentProblemError.StringTooLong(1, 11, 10)
      )
      errs.distinct.size shouldBe 2
    }
  }
}
