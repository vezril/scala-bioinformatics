package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DistanceMatrixProblemErrorSpec extends AnyFunSpec with Matchers {
  describe("DistanceMatrixProblemError.UnequalLengths") {
    it("carries all the differing lengths") {
      val err = DistanceMatrixProblemError.UnequalLengths(Vector(4, 4, 6))
      err.lengths shouldBe Vector(4, 4, 6)
    }
  }
}
