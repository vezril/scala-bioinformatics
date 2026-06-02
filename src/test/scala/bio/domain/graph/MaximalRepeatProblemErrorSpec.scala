package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaximalRepeatProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("MaximalRepeatProblemError") {
    it("constructs SequenceTooLong carrying length and max") {
      val err: MaximalRepeatProblemError = MaximalRepeatProblemError.SequenceTooLong(1001, 1000)
      err shouldBe MaximalRepeatProblemError.SequenceTooLong(1001, 1000)
    }

    it("constructs NonPositiveMinLength carrying the minimum length") {
      val err: MaximalRepeatProblemError = MaximalRepeatProblemError.NonPositiveMinLength(0)
      err shouldBe MaximalRepeatProblemError.NonPositiveMinLength(0)
    }
  }
}
