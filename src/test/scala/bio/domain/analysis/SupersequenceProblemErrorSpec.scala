package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SupersequenceProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("SupersequenceProblemError") {
    it("constructs SequenceTooLong carrying length and max") {
      val err: SupersequenceProblemError = SupersequenceProblemError.SequenceTooLong(1001, 1000)
      err shouldBe SupersequenceProblemError.SequenceTooLong(1001, 1000)
    }
  }
}
