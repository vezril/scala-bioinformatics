package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RestrictionMapProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("RestrictionMapProblemError") {
    it("constructs InvalidSize carrying the size") {
      val err: RestrictionMapProblemError = RestrictionMapProblemError.InvalidSize(2)
      err shouldBe RestrictionMapProblemError.InvalidSize(2)
    }

    it("constructs NonPositiveDistance carrying the index and value") {
      val err: RestrictionMapProblemError =
        RestrictionMapProblemError.NonPositiveDistance(1, -1)
      err shouldBe RestrictionMapProblemError.NonPositiveDistance(1, -1)
    }
  }
}
