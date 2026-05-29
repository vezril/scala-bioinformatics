package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EditDistanceAlignmentProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("EditDistanceAlignmentProblemError.LeftTooLong") {
    it("carries the offending length and the configured cap") {
      val err = EditDistanceAlignmentProblemError.LeftTooLong(1001, 1000)
      err.length shouldBe 1001
      err.max shouldBe 1000
    }

    it("is value-equal when length and max match") {
      EditDistanceAlignmentProblemError.LeftTooLong(1001, 1000) shouldBe
        EditDistanceAlignmentProblemError.LeftTooLong(1001, 1000)
    }
  }

  describe("EditDistanceAlignmentProblemError.RightTooLong") {
    it("carries the offending length and the configured cap") {
      val err = EditDistanceAlignmentProblemError.RightTooLong(2048, 1000)
      err.length shouldBe 2048
      err.max shouldBe 1000
    }

    it("is value-equal when length and max match") {
      EditDistanceAlignmentProblemError.RightTooLong(2048, 1000) shouldBe
        EditDistanceAlignmentProblemError.RightTooLong(2048, 1000)
    }
  }

  describe("EditDistanceAlignmentProblemError as an ADT") {
    it("makes LeftTooLong and RightTooLong distinct subtypes of the sealed trait") {
      val errs: List[EditDistanceAlignmentProblemError] = List(
        EditDistanceAlignmentProblemError.LeftTooLong(1001, 1000),
        EditDistanceAlignmentProblemError.RightTooLong(1001, 1000)
      )
      errs.distinct.size shouldBe 2
    }
  }
}
