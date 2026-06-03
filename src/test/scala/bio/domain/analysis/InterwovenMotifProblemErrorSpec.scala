package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InterwovenMotifProblemErrorSpec extends AnyFunSpec with Matchers {
  describe("InterwovenMotifProblemError.TooManyPatterns") {
    it("carries the offending count and the maximum allowed") {
      val err = InterwovenMotifProblemError.TooManyPatterns(12, 10)
      err.count shouldBe 12
      err.max shouldBe 10
    }
  }
}
