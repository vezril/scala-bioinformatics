package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TransitionTransversionProblemErrorSpec extends AnyFunSpec with Matchers {
  describe("TransitionTransversionProblemError.LengthMismatch") {
    it("carries the two differing lengths") {
      val err = TransitionTransversionProblemError.LengthMismatch(7, 9)
      err.firstLength shouldBe 7
      err.secondLength shouldBe 9
    }
  }
}
