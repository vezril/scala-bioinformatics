package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InterwovenMotifMatrixSpec extends AnyFunSpec with Matchers {
  describe("InterwovenMotifMatrix.format") {
    it("renders each row space-separated, rows newline-joined") {
      InterwovenMotifMatrix(
        Vector(Vector(0, 0, 1), Vector(0, 1, 0), Vector(1, 0, 0))
      ).format shouldBe "0 0 1\n0 1 0\n1 0 0"
    }

    it("renders an empty matrix as the empty string") {
      InterwovenMotifMatrix(Vector.empty).format shouldBe ""
    }
  }
}
