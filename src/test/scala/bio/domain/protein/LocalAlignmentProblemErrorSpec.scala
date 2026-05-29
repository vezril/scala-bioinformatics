package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LocalAlignmentProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("LocalAlignmentProblemError.LeftTooLong") {
    it("carries the offending length and the configured cap") {
      val err = LocalAlignmentProblemError.LeftTooLong(1001, 1000)
      err.length shouldBe 1001
      err.max shouldBe 1000
    }

    it("is value-equal when length and max match") {
      LocalAlignmentProblemError.LeftTooLong(1001, 1000) shouldBe
        LocalAlignmentProblemError.LeftTooLong(1001, 1000)
    }
  }

  describe("LocalAlignmentProblemError.RightTooLong") {
    it("carries the offending length and the configured cap") {
      val err = LocalAlignmentProblemError.RightTooLong(2048, 1000)
      err.length shouldBe 2048
      err.max shouldBe 1000
    }

    it("is value-equal when length and max match") {
      LocalAlignmentProblemError.RightTooLong(2048, 1000) shouldBe
        LocalAlignmentProblemError.RightTooLong(2048, 1000)
    }
  }

  describe("LocalAlignmentProblemError as an ADT") {
    it("makes LeftTooLong and RightTooLong distinct subtypes of the sealed trait") {
      val errs: List[LocalAlignmentProblemError] = List(
        LocalAlignmentProblemError.LeftTooLong(1001, 1000),
        LocalAlignmentProblemError.RightTooLong(1001, 1000)
      )
      errs.distinct.size shouldBe 2
    }
  }
}
