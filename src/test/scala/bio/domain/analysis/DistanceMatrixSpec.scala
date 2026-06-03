package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DistanceMatrixSpec extends AnyFunSpec with Matchers {
  describe("DistanceMatrix.format") {
    it("renders the matrix to five decimal places") {
      DistanceMatrix(
        Vector(Vector(0.0, 0.4), Vector(0.4, 0.0))
      ).format shouldBe "0.00000 0.40000\n0.40000 0.00000"
    }

    it("renders an empty matrix as the empty string") {
      DistanceMatrix(Vector.empty).format shouldBe ""
    }
  }
}
