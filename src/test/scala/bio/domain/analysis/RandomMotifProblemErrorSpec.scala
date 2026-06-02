package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RandomMotifProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("RandomMotifProblemError") {
    it("constructs MotifTooLong carrying length and max") {
      val err: RandomMotifProblemError =
        RandomMotifProblemError.MotifTooLong(11, 10)
      err shouldBe RandomMotifProblemError.MotifTooLong(11, 10)
    }

    it("constructs NonPositiveTrials carrying the trial count") {
      val err: RandomMotifProblemError =
        RandomMotifProblemError.NonPositiveTrials(0)
      err shouldBe RandomMotifProblemError.NonPositiveTrials(0)
    }

    it("constructs TooManyTrials carrying the trial count and max") {
      val err: RandomMotifProblemError =
        RandomMotifProblemError.TooManyTrials(100001, 100000)
      err shouldBe RandomMotifProblemError.TooManyTrials(100001, 100000)
    }
  }
}
