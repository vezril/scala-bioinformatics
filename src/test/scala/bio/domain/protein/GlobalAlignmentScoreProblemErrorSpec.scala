package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GlobalAlignmentScoreProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("GlobalAlignmentScoreProblemError.LeftTooLong") {
    it("carries the offending length and the configured cap") {
      val err = GlobalAlignmentScoreProblemError.LeftTooLong(1001, 1000)
      err.length shouldBe 1001
      err.max shouldBe 1000
    }

    it("is value-equal when length and max match") {
      GlobalAlignmentScoreProblemError.LeftTooLong(1001, 1000) shouldBe
        GlobalAlignmentScoreProblemError.LeftTooLong(1001, 1000)
    }
  }

  describe("GlobalAlignmentScoreProblemError.RightTooLong") {
    it("carries the offending length and the configured cap") {
      val err = GlobalAlignmentScoreProblemError.RightTooLong(2048, 1000)
      err.length shouldBe 2048
      err.max shouldBe 1000
    }

    it("is value-equal when length and max match") {
      GlobalAlignmentScoreProblemError.RightTooLong(2048, 1000) shouldBe
        GlobalAlignmentScoreProblemError.RightTooLong(2048, 1000)
    }
  }

  describe("GlobalAlignmentScoreProblemError as an ADT") {
    it("makes LeftTooLong and RightTooLong distinct subtypes of the sealed trait") {
      val errs: List[GlobalAlignmentScoreProblemError] = List(
        GlobalAlignmentScoreProblemError.LeftTooLong(1001, 1000),
        GlobalAlignmentScoreProblemError.RightTooLong(1001, 1000)
      )
      errs.distinct.size shouldBe 2
    }
  }
}
