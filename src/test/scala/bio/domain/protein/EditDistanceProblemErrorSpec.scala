package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EditDistanceProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("EditDistanceProblemError.LeftTooLong") {
    it("carries the offending length and the configured cap") {
      val err = EditDistanceProblemError.LeftTooLong(1001, 1000)
      err.length shouldBe 1001
      err.max shouldBe 1000
    }

    it("is value-equal when length and max match") {
      EditDistanceProblemError.LeftTooLong(1001, 1000) shouldBe
        EditDistanceProblemError.LeftTooLong(1001, 1000)
    }
  }

  describe("EditDistanceProblemError.RightTooLong") {
    it("carries the offending length and the configured cap") {
      val err = EditDistanceProblemError.RightTooLong(2048, 1000)
      err.length shouldBe 2048
      err.max shouldBe 1000
    }

    it("is value-equal when length and max match") {
      EditDistanceProblemError.RightTooLong(2048, 1000) shouldBe
        EditDistanceProblemError.RightTooLong(2048, 1000)
    }
  }

  describe("EditDistanceProblemError as an ADT") {
    it("makes LeftTooLong and RightTooLong distinct subtypes of the sealed trait") {
      val errs: List[EditDistanceProblemError] = List(
        EditDistanceProblemError.LeftTooLong(1001, 1000),
        EditDistanceProblemError.RightTooLong(1001, 1000)
      )
      errs.distinct.size shouldBe 2
    }
  }
}
