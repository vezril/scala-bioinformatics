package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RandomMatchProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("RandomMatchProblemError.DnaTooLong") {
    it("carries the offending length and the maximum") {
      val err = RandomMatchProblemError.DnaTooLong(150, 100)
      err.length shouldBe 150
      err.max shouldBe 100
    }
  }

  describe("RandomMatchProblemError.TooManyGcContents") {
    it("carries the offending size and the maximum") {
      val err = RandomMatchProblemError.TooManyGcContents(25, 20)
      err.size shouldBe 25
      err.max shouldBe 20
    }
  }
}
