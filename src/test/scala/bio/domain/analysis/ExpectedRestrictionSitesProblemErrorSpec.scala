package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ExpectedRestrictionSitesProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("ExpectedRestrictionSitesProblemError") {
    it("constructs MotifTooLong carrying length and max") {
      val err: ExpectedRestrictionSitesProblemError =
        ExpectedRestrictionSitesProblemError.MotifTooLong(12, 10)
      err shouldBe ExpectedRestrictionSitesProblemError.MotifTooLong(12, 10)
    }

    it("constructs OddMotifLength carrying the length") {
      val err: ExpectedRestrictionSitesProblemError =
        ExpectedRestrictionSitesProblemError.OddMotifLength(3)
      err shouldBe ExpectedRestrictionSitesProblemError.OddMotifLength(3)
    }

    it("constructs NonPositiveLength carrying the length") {
      val err: ExpectedRestrictionSitesProblemError =
        ExpectedRestrictionSitesProblemError.NonPositiveLength(0)
      err shouldBe ExpectedRestrictionSitesProblemError.NonPositiveLength(0)
    }

    it("constructs LengthTooLarge carrying the length and max") {
      val err: ExpectedRestrictionSitesProblemError =
        ExpectedRestrictionSitesProblemError.LengthTooLarge(1000001, 1000000)
      err shouldBe ExpectedRestrictionSitesProblemError.LengthTooLarge(1000001, 1000000)
    }

    it("constructs TooManyGcContents carrying the size and max") {
      val err: ExpectedRestrictionSitesProblemError =
        ExpectedRestrictionSitesProblemError.TooManyGcContents(21, 20)
      err shouldBe ExpectedRestrictionSitesProblemError.TooManyGcContents(21, 20)
    }
  }
}
