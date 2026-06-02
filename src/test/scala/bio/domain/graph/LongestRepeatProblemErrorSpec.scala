package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LongestRepeatProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("LongestRepeatProblemError") {
    it("constructs NonPositiveK carrying k") {
      val err: LongestRepeatProblemError = LongestRepeatProblemError.NonPositiveK(0)
      err shouldBe LongestRepeatProblemError.NonPositiveK(0)
    }

    it("constructs TextTooLong carrying length and max") {
      val err: LongestRepeatProblemError =
        LongestRepeatProblemError.TextTooLong(20002, 20001)
      err shouldBe LongestRepeatProblemError.TextTooLong(20002, 20001)
    }

    it("constructs EdgeOutOfBounds carrying index, start, length, and text length") {
      val err: LongestRepeatProblemError =
        LongestRepeatProblemError.EdgeOutOfBounds(0, 5, 1, 3)
      err shouldBe LongestRepeatProblemError.EdgeOutOfBounds(0, 5, 1, 3)
    }
  }
}
