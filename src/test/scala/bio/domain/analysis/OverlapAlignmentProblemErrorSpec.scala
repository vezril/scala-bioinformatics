package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OverlapAlignmentProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("OverlapAlignmentProblemError") {
    it("is a sealed trait with STooLong and TTooLong cases carrying length and max") {
      val sErr: OverlapAlignmentProblemError =
        OverlapAlignmentProblemError.STooLong(10001, 10000)
      val tErr: OverlapAlignmentProblemError =
        OverlapAlignmentProblemError.TTooLong(10001, 10000)

      sErr shouldBe OverlapAlignmentProblemError.STooLong(10001, 10000)
      tErr shouldBe OverlapAlignmentProblemError.TTooLong(10001, 10000)

      OverlapAlignmentProblemError.STooLong(10001, 10000).length shouldBe 10001
      OverlapAlignmentProblemError.STooLong(10001, 10000).max shouldBe 10000
      OverlapAlignmentProblemError.TTooLong(5, 4).length shouldBe 5
      OverlapAlignmentProblemError.TTooLong(5, 4).max shouldBe 4
    }
  }
}
