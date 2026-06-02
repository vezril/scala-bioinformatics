package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SemiglobalAlignmentProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("SemiglobalAlignmentProblemError") {
    it("constructs STooLong and TTooLong as SemiglobalAlignmentProblemError subtypes") {
      val sErr: SemiglobalAlignmentProblemError =
        SemiglobalAlignmentProblemError.STooLong(10001, 10000)
      val tErr: SemiglobalAlignmentProblemError =
        SemiglobalAlignmentProblemError.TTooLong(10001, 10000)

      sErr shouldBe SemiglobalAlignmentProblemError.STooLong(10001, 10000)
      tErr shouldBe SemiglobalAlignmentProblemError.TTooLong(10001, 10000)
    }
  }
}
