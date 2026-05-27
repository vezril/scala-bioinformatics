package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RnaSplicingProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("RnaSplicingProblemError.EmptyIntron") {
    it("carries the 0-indexed position of the offending intron") {
      RnaSplicingProblemError.EmptyIntron(2).index shouldBe 2
    }
  }
}
