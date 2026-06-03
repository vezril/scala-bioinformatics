package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WobbleMatchingProblemErrorSpec extends AnyFunSpec with Matchers {
  describe("WobbleMatchingProblemError.SequenceTooLong") {
    it("carries the offending length and the maximum allowed length") {
      val err = WobbleMatchingProblemError.SequenceTooLong(250, 200)
      err.length shouldBe 250
      err.max shouldBe 200
    }
  }
}
