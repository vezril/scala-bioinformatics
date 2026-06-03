package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaxGapProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("MaxGapProblemError") {
    it("constructs SequenceTooLong carrying length and max") {
      val err: MaxGapProblemError = MaxGapProblemError.SequenceTooLong(5001, 5000)
      err shouldBe MaxGapProblemError.SequenceTooLong(5001, 5000)
    }
  }
}
