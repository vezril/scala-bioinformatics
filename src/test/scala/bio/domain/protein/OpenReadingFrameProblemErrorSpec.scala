package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OpenReadingFrameProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("OpenReadingFrameProblemError") {
    it("constructs SequenceTooLong as an OpenReadingFrameProblemError carrying length and max") {
      val err: OpenReadingFrameProblemError =
        OpenReadingFrameProblemError.SequenceTooLong(1001, 1000)

      err shouldBe OpenReadingFrameProblemError.SequenceTooLong(1001, 1000)
    }
  }
}
