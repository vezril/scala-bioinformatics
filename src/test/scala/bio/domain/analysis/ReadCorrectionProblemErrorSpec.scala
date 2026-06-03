package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReadCorrectionProblemErrorSpec extends AnyFunSpec with Matchers {
  describe("ReadCorrectionProblemError.TooManyReads") {
    it("carries the offending count and the maximum allowed") {
      val err = ReadCorrectionProblemError.TooManyReads(1500, 1000)
      err.count shouldBe 1500
      err.max shouldBe 1000
    }
  }
}
