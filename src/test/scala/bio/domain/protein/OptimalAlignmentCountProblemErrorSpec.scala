package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OptimalAlignmentCountProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("OptimalAlignmentCountProblemError.LeftTooLong") {
    it("carries the offending length and the configured cap") {
      val err = OptimalAlignmentCountProblemError.LeftTooLong(1001, 1000)
      err.length shouldBe 1001
      err.max shouldBe 1000
    }

    it("is value-equal when length and max match") {
      OptimalAlignmentCountProblemError.LeftTooLong(1001, 1000) shouldBe
        OptimalAlignmentCountProblemError.LeftTooLong(1001, 1000)
    }
  }

  describe("OptimalAlignmentCountProblemError.RightTooLong") {
    it("carries the offending length and the configured cap") {
      val err = OptimalAlignmentCountProblemError.RightTooLong(2048, 1000)
      err.length shouldBe 2048
      err.max shouldBe 1000
    }

    it("is value-equal when length and max match") {
      OptimalAlignmentCountProblemError.RightTooLong(2048, 1000) shouldBe
        OptimalAlignmentCountProblemError.RightTooLong(2048, 1000)
    }
  }

  describe("OptimalAlignmentCountProblemError as an ADT") {
    it("makes LeftTooLong and RightTooLong distinct subtypes of the sealed trait") {
      val errs: List[OptimalAlignmentCountProblemError] = List(
        OptimalAlignmentCountProblemError.LeftTooLong(1001, 1000),
        OptimalAlignmentCountProblemError.RightTooLong(1001, 1000)
      )
      errs.distinct.size shouldBe 2
    }
  }
}
