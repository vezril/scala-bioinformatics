package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RestrictionSiteProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("RestrictionSiteProblemError") {
    it("constructs SequenceTooLong as a RestrictionSiteProblemError carrying length and max") {
      val err: RestrictionSiteProblemError =
        RestrictionSiteProblemError.SequenceTooLong(1001, 1000)

      err shouldBe RestrictionSiteProblemError.SequenceTooLong(1001, 1000)
    }
  }
}
